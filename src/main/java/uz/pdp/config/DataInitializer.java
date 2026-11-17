package uz.pdp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.entity.Cart;
import uz.pdp.entity.User;
import uz.pdp.enums.Role;
import uz.pdp.repo.CartRepo;
import uz.pdp.repo.UserRepo;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepo userRepository;
    private final CartRepo cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@gmail.com";

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setEmail(adminEmail);
            admin.setPhone("+998901234567");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            User savedAdmin = userRepository.save(admin);

            Cart adminCart = new Cart();
            adminCart.setUser(savedAdmin);
            cartRepository.save(adminCart);

            log.info("Default ADMIN account initialized: {}", adminEmail);
        }
    }
}
