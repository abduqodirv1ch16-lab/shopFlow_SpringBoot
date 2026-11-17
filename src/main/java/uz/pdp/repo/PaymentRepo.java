package uz.pdp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    Optional<Payment> findById(Long id);
}
