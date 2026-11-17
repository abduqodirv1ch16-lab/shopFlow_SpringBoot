package uz.pdp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.dto.request.LoginRequest;
import uz.pdp.dto.request.RegisterRequest;
import uz.pdp.dto.response.ApiResponse;
import uz.pdp.dto.response.AuthResponse;
import uz.pdp.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse registered = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Foydalanuvchi muvaffaqiyatli ro'yxatdan o'tdi", registered));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse loggedIn = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Tizimga muvaffaqiyatli kirildi", loggedIn));
    }
}
