package uz.pdp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Ism kiritilishi shart")
    private String firstname;

    @NotBlank(message = "Familiya kiritilishi shart")
    private String lastname;

    @NotBlank(message = "Email kiritilishi shart")
    @Email(message = "Email formati noto'g'ri")
    private String email;

    @NotBlank(message = "Telefon raqam kiritilishi shart")
    private String phone;

    private String avatarUrl;

    @NotBlank(message = "Parol kiritilishi shart")
    @Size(min = 6, message = "Parol kamida 6 ta belgidan iborat bo'lishi kerak")
    private String password;
}
