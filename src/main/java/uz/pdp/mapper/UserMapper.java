package uz.pdp.mapper;

import org.springframework.stereotype.Component;
import uz.pdp.dto.request.RegisterRequest;
import uz.pdp.dto.response.UserResponse;
import uz.pdp.entity.User;
import uz.pdp.enums.Role;

@Component
public class UserMapper {
    public User toEntity(RegisterRequest request) {
        if (request == null) return null;
        return User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .avatarUrl(request.getAvatarUrl())
                .role(Role.USER)
                .active(true)
                .build();
    }

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
