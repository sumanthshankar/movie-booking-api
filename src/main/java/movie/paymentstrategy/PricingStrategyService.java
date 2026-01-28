package movie.paymentstrategy;

import movie.model.Show;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingStrategyService {

    private final List<PricingStrategy> pricingStrategies = List.of(
            new ThirdSeatDiscountStrategy(),
            new AfternoonSeatDiscountStrategy()
    );

    public BigDecimal calculateTotal(List<Long> seats, Show show) {
        return pricingStrategies.stream()
                .filter(strategy -> strategy.isApplicable(seats, show))
                .findFirst()
                .map(strategy -> strategy.calculatePrice(seats, show))
                .orElseGet(() -> basePrice(seats, show));
    }

    private BigDecimal basePrice(List<Long> seats, Show show) {
        return show.getPrice()
                   .multiply(BigDecimal.valueOf(seats.size()));
    }
}
