package uz.pdp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message = "Mahsulot nomi bo'sh bo'lishi mumkin emas")
    private String name;

    private String description;

    @NotNull(message = "Mahsulot narxi kiritilishi shart")
    @DecimalMin(value = "0.0", inclusive = false, message = "Mahsulot narxi 0 dan katta bo'lishi kerak")
    private BigDecimal price;

    @NotNull(message = "Ombordagi soni kiritilishi shart")
    @Min(value = 0, message = "Ombordagi soni manfiy bo'lishi mumkin emas")
    private Integer quantity;

    private List<String> imageUrls;

    @NotNull(message = "Kategoriya ID ko'rsatilishi shart")
    private Long categoryId;
}
