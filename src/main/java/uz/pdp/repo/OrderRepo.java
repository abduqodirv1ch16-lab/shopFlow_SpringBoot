package uz.pdp.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.pdp.entity.Order;
import uz.pdp.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long> {
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Optional<Order> findById(Long id);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("""
        SELECT oi.product.id, oi.productName, SUM(oi.quantity) AS totalSold
        FROM OrderItem oi
        JOIN oi.order o
        WHERE o.status <> :cancelledStatus
        GROUP BY oi.product.id, oi.productName
        ORDER BY totalSold DESC
    """)
    List<Object[]> findTopSellingProducts(
            @Param("cancelledStatus") OrderStatus cancelledStatus,
            Pageable pageable
    );
}
