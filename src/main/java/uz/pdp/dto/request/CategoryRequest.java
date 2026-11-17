package uz.pdp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank(message = "Kategoriya nomi kiritilishi shart")
    private String name;

    private String description;
}
