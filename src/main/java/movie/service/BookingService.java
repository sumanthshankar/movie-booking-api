package movie.service;

import lombok.extern.slf4j.Slf4j;
import movie.cache.DistributedLockService;
import movie.dto.BookingRequest;
import movie.dto.BookingResponse;
import movie.exception.PaymentException;
import movie.exception.SeatException;
import movie.exception.ShowException;
import movie.model.*;
import movie.payment.PaymentGateway;
import movie.paymentstrategy.PricingStrategyService;
import movie.repository.BookingRepository;
import movie.repository.SeatRepository;
import movie.repository.ShowRepository;
import movie.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookingService {
    private final SeatRepository seatRepository;

    private final ShowRepository showRepository;

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final PaymentGateway paymentGateway;

    private final PricingStrategyService pricingStrategyService;

    private final DistributedLockService distributedLockService;

    public BookingService(SeatRepository seatRepository,
                          ShowRepository showRepository,
                          BookingRepository bookingRepository,
                          UserRepository userRepository,
                          PaymentGateway paymentGateway,
                          PricingStrategyService pricingStrategyService,
                          DistributedLockService distributedLockService) {
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.paymentGateway = paymentGateway;
        this.pricingStrategyService = pricingStrategyService;
        this.distributedLockService = distributedLockService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingResponse bookingTickets(BookingRequest bookingRequest) {
        try {
            log.info("Initiating booking for User: {} at Show: {}" , bookingRequest.getUserEmail(), bookingRequest.getShowId());

            // Acquire lock for each seat
            bookingRequest.getSeatIds().forEach(seatId ->
                    distributedLockService.acquireLock(bookingRequest.getShowId(), seatId)
            );

            // Check for user if user not found create
            Optional<User> getUser = userRepository.findByEmail(bookingRequest.getUserEmail());

            User user = null;
            if(getUser.isEmpty()) {
                user = new User();
                user.setEmail(bookingRequest.getUserEmail());
                userRepository.save(user);
            } else {
                user = getUser.get();
            }

            // Check for show if show not found create throw exception
            Optional<Show> getShow = showRepository.findById(bookingRequest.getShowId());
            if(getShow.isEmpty()) {
                throw new ShowException("Show not found");
            }

            Show show = getShow.get();

            // Get selected seats and check if seats are available
            List<Seat> seats = seatRepository.findByIdIn(bookingRequest.getSeatIds());
            if (seats.size() != bookingRequest.getSeatIds().size()) {
                throw new SeatException("Seats are not available for this show");
            }

            // For the available seats acquire a lock
            for (Seat seat : seats) {
                if (seat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new SeatException("Seat " + seat.getSeatNumber() + " is not available.");
                }
                seat.setStatus(SeatStatus.LOCKED);
                seat.setShow(show);
            }

            // Optimistic lock check happens using @Version.
            seatRepository.saveAll(seats);

            // Calculate the cost and provide discount if more than 2 seats are selected
            BigDecimal totalAmount = pricingStrategyService.calculateTotal(bookingRequest.getSeatIds(), show);

            SecureRandom secureRandom = new SecureRandom();
            StringBuilder stringBuilder = new StringBuilder(10);
            for (int i = 0; i < 10; i++) {
                stringBuilder.append(secureRandom.nextInt(10));
            }

            // Create a booking with pending status
            Booking booking = new Booking();
            booking.setTotalAmount(totalAmount);
            booking.setBookedAt(LocalDateTime.now());
            booking.setBookingReference(stringBuilder.toString());
            booking.setBookingStatus(BookingStatus.PENDING);
            booking.setUser(user);
            booking.setShow(show);

            bookingRepository.save(booking);

            boolean paymentSuccess = paymentGateway.processPayment(booking.getId(), booking.getTotalAmount());

            if(!paymentSuccess) {
                throw new PaymentException("Payment is not successfull.");
            }

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            seats.forEach(seat -> seat.setStatus(SeatStatus.BOOKED));

            log.info("Booking is successfull: {}" , booking.getId());

            BookingResponse bookingResponse = new BookingResponse();
            bookingResponse.setBookingId(booking.getId());
            bookingResponse.setBookingReference(bookingResponse.getBookingReference());
            bookingResponse.setStatus(BookingStatus.CONFIRMED);
            bookingResponse.setTimestamp(LocalDateTime.now());

            return bookingResponse;
        } catch (Exception e) {
            log.error("Booking failed for User: {}. Reason: {}" , bookingRequest.getUserEmail(), e.getMessage());
            throw e;
        } finally {
            // Release distributed lock
            bookingRequest.getSeatIds().forEach(seatId ->
                    distributedLockService.releaseLock(bookingRequest.getShowId(), seatId));
        }
    }
}