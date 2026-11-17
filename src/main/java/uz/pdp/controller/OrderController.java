package uz.pdp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.dto.request.OrderRequest;
import uz.pdp.dto.response.ApiResponse;
import uz.pdp.dto.response.OrderResponse;
import uz.pdp.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Buyurtma yaratildi", order)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderResponse> orders = orderService.getMyOrder(pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Buyurtmalar tarixi", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse orderById = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Buyurtma ma'lumotlari", orderById));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelMyOrder(@PathVariable Long id) {
        OrderResponse cancelled = orderService.cancelMyOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Buyurtma bekor qilindi", cancelled));
    }
}
