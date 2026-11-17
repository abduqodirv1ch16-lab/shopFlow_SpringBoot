package uz.pdp.service;

import uz.pdp.dto.request.LoginRequest;
import uz.pdp.dto.request.RegisterRequest;
import uz.pdp.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
