package uz.pdp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.dto.response.UserResponse;
import uz.pdp.entity.User;
import uz.pdp.exception.BadRequestException;
import uz.pdp.exception.ResourceNotFoundException;
import uz.pdp.mapper.UserMapper;
import uz.pdp.repo.UserRepo;
import uz.pdp.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Joriy foydalanuvchi topilmadi: " + email));
    }

    @Override
    public UserResponse getMyProfile() {
        User user = getCurrentAuthenticatedUser();
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(String firstName, String lastName, String phone, String email, String newPassword) {
        User user = getCurrentAuthenticatedUser();

        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName.trim());
        }
        if (phone != null && !phone.isBlank()) {
            user.setPhone(phone.trim());
        }
        if (email != null && !email.isBlank()) {
            String newEmail = email.toLowerCase().trim();
            if (!newEmail.equals(user.getEmail()) && userRepo.existsByEmail(newEmail)) {
                throw new DataIntegrityViolationException("Ushbu email boshqa foydalanuvchi tomonidan ro'yxatdan o'tkazilgan: " + newEmail);
            }
            user.setEmail(newEmail);
        }
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userMapper.toResponse(userRepo.save(user));
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        User user = getCurrentAuthenticatedUser();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Joriy parol noto'g'ri kiritildi");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("Yangi parol kamida 6 ta belgidan iborat bo'lishi kerak");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long userId, boolean active) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi, ID: " + userId));
        user.setActive(active);
        return userMapper.toResponse(userRepo.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateMyAvatar(String avatarUrl) {
        User user = getCurrentAuthenticatedUser();
        user.setAvatarUrl(avatarUrl);
        return userMapper.toResponse(userRepo.save(user));
    }
}
