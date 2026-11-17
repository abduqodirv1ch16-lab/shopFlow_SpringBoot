package uz.pdp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.pdp.dto.response.UserResponse;
import uz.pdp.entity.User;

public interface UserService {
    User getCurrentAuthenticatedUser();
    UserResponse getMyProfile();
    UserResponse updateMyProfile(String firstName, String lastName, String phone, String email, String password);
    void changePassword(String oldPassword, String newPassword);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUserStatus(Long userId, boolean active);
    UserResponse updateMyAvatar(String avatarUrl);
}
