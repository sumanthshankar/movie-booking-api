package movie.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import lombok.extern.slf4j.Slf4j;
import movie.dto.PaymentRequest;
import movie.exception.PaymentException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StripeService {

    public Charge charge(PaymentRequest paymentRequest) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
            params.put("currency", "usd");
            params.put("description", "Booking ID: " + paymentRequest.getBookingId());
            params.put("source", "tok_visa"); // test token

            return Charge.create(params);
        } catch (StripeException e) {
            log.error("Exception occurred while processing stripe payment: {}", e.getStripeError());
            throw new PaymentException("Error occurred while processing Stripe Payment");
        }
    }
}
