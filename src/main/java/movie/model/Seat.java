package movie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Seat {
    @Id
    @GeneratedValue
    private Long id;

    private String seatNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private Show show;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Version
    private Long version;
}
