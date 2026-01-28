package movie.dto;

import lombok.Getter;
import lombok.Setter;
import movie.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponse {
    private Long bookingId;

    private BookingStatus status;

    private String bookingReference;

    private LocalDateTime timestamp;
}
