package movie.paymentstrategy;

import movie.model.Show;

import java.math.BigDecimal;
import java.util.List;

public class AfternoonSeatDiscountStrategy implements PricingStrategy {
    @Override
    public boolean isApplicable(List<Long> seats, Show show) {
        return show.getShowTime().getHour() < 17;
    }

    @Override
    public BigDecimal calculatePrice(List<Long> seats, Show show) {
        BigDecimal totalPrice = show.getPrice().multiply(BigDecimal.valueOf(seats.size()));
        return totalPrice.multiply(BigDecimal.valueOf(0.80));
    }
}
