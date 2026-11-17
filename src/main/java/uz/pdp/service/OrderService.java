package uz.pdp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.pdp.dto.request.OrderRequest;
import uz.pdp.dto.response.OrderResponse;
import uz.pdp.enums.OrderStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    Page<OrderResponse> getMyOrder(Pageable pageable);
    OrderResponse getOrderById(Long orderId);
    OrderResponse cancelMyOrder(Long orderId);
    Page<OrderResponse> getAllOrders(Pageable pageable);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
    Map<String, Object> getAdminStatistics();
    List<Map<String, Object>> getTopSellingProducts(int limit);
}
