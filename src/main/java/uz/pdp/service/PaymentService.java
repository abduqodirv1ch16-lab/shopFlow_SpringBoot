package uz.pdp.service;

import uz.pdp.dto.request.PaymentRequest;
import uz.pdp.entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment processTestPayment(Long orderId, PaymentRequest request);
    Payment getPaymentById(Long paymentId);
    List<Payment> getPaymentsByOrderId(Long orderId);
}
