package movie.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookingRequest {
    @NotNull(message = "User email is required")
    private String userEmail;

    @NotNull(message = "Show ID is required")
    private Long showId;

    @NotEmpty(message = "Must select at least one seat")
    private List<Long> seatIds;
}