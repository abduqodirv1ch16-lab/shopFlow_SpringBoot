package uz.pdp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.dto.request.PaymentRequest;
import uz.pdp.dto.response.ApiResponse;
import uz.pdp.entity.Payment;
import uz.pdp.service.PaymentService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}/pay")
    public ResponseEntity<ApiResponse<Map<String, Object>>> payForOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.processTestPayment(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("To'lov qayta ishlandi", toPaymentDto(payment)));
    }

    @GetMapping("/orders/{orderId}/payments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOrderPayments(@PathVariable Long orderId) {
        List<Map<String, Object>> payments = paymentService.getPaymentsByOrderId(orderId).stream()
                .map(this::toPaymentDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Buyurtma to'lovlari tarixi", payments));
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPaymentById(@PathVariable Long paymentId) {
        Payment paymentById = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(ApiResponse.success("To'lov tafsilotlari", toPaymentDto(paymentById)));
    }

    private Map<String, Object> toPaymentDto(Payment payment) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("paymentId", payment.getId());
        dto.put("orderId", payment.getOrder().getId());
        dto.put("amount", payment.getAmount());
        dto.put("paymentMethod", payment.getPaymentMethod());
        dto.put("paymentStatus", payment.getPaymentStatus());
        dto.put("createdAt", payment.getCreatedAt());
        return dto;
    }
}
