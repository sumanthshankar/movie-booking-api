package movie.paymentstrategy;

import movie.model.Show;

import java.math.BigDecimal;
import java.util.List;

public interface PricingStrategy {
    boolean isApplicable(List<Long> seats, Show show);

    BigDecimal calculatePrice(List<Long> seats, Show show);
}
