package uz.pdp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.entity.User;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
