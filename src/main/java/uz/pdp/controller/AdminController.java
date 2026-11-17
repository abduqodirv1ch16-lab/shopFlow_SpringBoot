package uz.pdp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.dto.response.ApiResponse;
import uz.pdp.dto.response.OrderResponse;
import uz.pdp.dto.response.UserResponse;
import uz.pdp.enums.OrderStatus;
import uz.pdp.service.OrderService;
import uz.pdp.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Foydalanuvchilar ro'yxati", users));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        UserResponse updatedUser = userService.updateUserStatus(id, active);
        String msg = active ? "Foydalanuvchi faollashtirildi" : "Foydalanuvchi bloklandi";
        return ResponseEntity.ok(ApiResponse.success(msg, updatedUser));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success("Barcha buyurtmalar ro'yxati", orders));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Buyurtma holati yangilandi: " + status, updatedOrder));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        Map<String, Object> stats = orderService.getAdminStatistics();
        return ResponseEntity.ok(ApiResponse.success("Tizim statistikasi", stats));
    }

    @GetMapping("/statistics/top-products")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> topSellingProducts = orderService.getTopSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.success("Eng ko'p sotilgan mahsulotlar", topSellingProducts));
    }
}
