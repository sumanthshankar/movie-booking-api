package movie.payment;

import com.stripe.model.Charge;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import movie.dto.PaymentRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class StripePaymentAdapter implements PaymentGateway {
    private final StripeService stripeService;

    public StripePaymentAdapter(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @Override
    @CircuitBreaker(name = "payment-gateway", fallbackMethod = "fallbackPayment")
    public boolean processPayment(Long bookingId, BigDecimal amount) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(amount);
        paymentRequest.setBookingId(bookingId);

        Charge charge = stripeService.charge(paymentRequest);

        log.info("Stripe payment is successfully: {}", charge.getPaymentIntent());

        return charge.getPaid();
    }

    public boolean fallbackPayment(Long bookingId, BigDecimal amount, Exception ex) {
        log.error("Stripe is down. Fallback logic for booking: {}", bookingId);
        return false;
    }
}
