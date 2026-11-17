package uz.pdp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.entity.CartItems;

import java.util.Optional;

public interface CartItemRepo extends JpaRepository<CartItems, Long> {
    Optional<CartItems> findByCartIdAndProductId(Long cartId, Long productId);
    Optional<CartItems> findById(Long id);
}
