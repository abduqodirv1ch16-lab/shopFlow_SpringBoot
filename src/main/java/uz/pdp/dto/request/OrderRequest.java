package uz.pdp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    @NotBlank(message = "Shahar nomini kiriting")
    private String city;

    @NotBlank(message = "Ko'cha nomini kiriting")
    private String street;

    @NotBlank(message = "Uy raqamini kiriting")
    private String houseNumber;

    private String additionalInfo;
}
