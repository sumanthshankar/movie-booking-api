package movie.paymentstrategy;

import movie.model.Show;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ThirdSeatDiscountStrategy implements PricingStrategy {
    @Override
    public boolean isApplicable(List<Long> seats, Show show) {
        return seats.size() >= 3;
    }

    @Override
    public BigDecimal calculatePrice(List<Long> seats, Show show) {
        // Total price for all tickets
        BigDecimal totalPrice = show.getPrice().multiply(BigDecimal.valueOf(seats.size()));

        // 50% discount price for the 3rd ticket
        BigDecimal thirdTicketDiscountPrice = show.getPrice().multiply(BigDecimal.valueOf(0.5));

        totalPrice = totalPrice.subtract(thirdTicketDiscountPrice);

        return totalPrice;
    }
}