package uz.pdp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.dto.request.LoginRequest;
import uz.pdp.dto.request.RegisterRequest;
import uz.pdp.dto.response.AuthResponse;
import uz.pdp.entity.Cart;
import uz.pdp.entity.User;
import uz.pdp.exception.ResourceNotFoundException;
import uz.pdp.mapper.UserMapper;
import uz.pdp.repo.CartRepo;
import uz.pdp.repo.UserRepo;
import uz.pdp.security.CustomUserDetailsService;
import uz.pdp.security.JwtService;
import uz.pdp.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final CartRepo cartRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (userRepo.existsByEmail(normalizedEmail)) {
            throw new DataIntegrityViolationException("Ushbu email allaqachon ro'yxatdan o'tgan: " + normalizedEmail);
        }

        User user = userMapper.toEntity(request);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepo.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepo.save(cart);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken, userMapper.toResponse(savedUser));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        User user = userRepo.findByEmail(normalizedEmail).orElseThrow(
                () -> new ResourceNotFoundException("Foydalanuvchi topilmadi")
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken, userMapper.toResponse(user));
    }
}
