package uz.pdp.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.pdp.enums.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String avatarUrl;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
}
