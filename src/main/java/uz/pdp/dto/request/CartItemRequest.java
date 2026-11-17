package uz.pdp.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {
    @NotNull(message = "Mahsulot ID raqamini kiriting")
    private Long productId;

    @NotNull(message = "Mahsulot miqdorini kiriting")
    @Min(value = 1, message = "Mahsulot soni kamida 1 ta bo'lishi kerak")
    private Integer quantity;
}
