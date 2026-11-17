package uz.pdp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.pdp.enums.PaymentMethod;

@Getter
@Setter
public class PaymentRequest {
    @NotNull(message = "To'lov turini tanlang (CARD yoki CASH)")
    private PaymentMethod paymentMethod;

    private boolean simulateSuccess = true;
}
