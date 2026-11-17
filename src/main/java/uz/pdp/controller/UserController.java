package uz.pdp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.dto.response.ApiResponse;
import uz.pdp.dto.response.UserResponse;
import uz.pdp.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        UserResponse myProfile = userService.getMyProfile();
        return ResponseEntity.ok(ApiResponse.success("Profil ma'lumotlari", myProfile));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(@RequestBody Map<String, String> body) {
        UserResponse userResponse = userService.updateMyProfile(
                body.get("firstName"),
                body.get("lastName"),
                body.get("phone"),
                body.get("email"),
                body.get("password")
        );
        return ResponseEntity.ok(ApiResponse.success("Profil ma'lumotlari yangilandi", userResponse));
    }

    @PatchMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyAvatar(@RequestBody Map<String, String> body) {
        UserResponse updated = userService.updateMyAvatar(body.get("avatarUrl"));
        return ResponseEntity.ok(ApiResponse.success("Profil rasmi yangilandi", updated));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody Map<String, String> body) {
        userService.changePassword(body.get("oldPassword"), body.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success("Parol muvaffaqiyatli o'zgartirildi"));
    }
}
