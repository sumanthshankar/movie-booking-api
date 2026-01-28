package movie.payment;

import java.math.BigDecimal;

public interface PaymentGateway {
    boolean processPayment(Long bookingId, BigDecimal amount);
}
