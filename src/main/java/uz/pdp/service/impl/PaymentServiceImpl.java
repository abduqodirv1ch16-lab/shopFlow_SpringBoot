package uz.pdp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.dto.request.PaymentRequest;
import uz.pdp.entity.Order;
import uz.pdp.entity.Payment;
import uz.pdp.entity.User;
import uz.pdp.enums.OrderStatus;
import uz.pdp.enums.PaymentStatus;
import uz.pdp.enums.Role;
import uz.pdp.exception.BadRequestException;
import uz.pdp.exception.ResourceNotFoundException;
import uz.pdp.repo.OrderRepo;
import uz.pdp.repo.PaymentRepo;
import uz.pdp.service.PaymentService;
import uz.pdp.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserService userService;
    private final OrderRepo orderRepo;
    private final PaymentRepo paymentRepo;

    @Override
    @Transactional
    public Payment processTestPayment(Long orderId, PaymentRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyurtma topilmadi, ID: " + orderId));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Siz boshqa foydalanuvchining buyurtmasi uchun to'lov qila olmaysiz!");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Ushbu buyurtma uchun to'lov qilib bo'lmaydi. Joriy holati: " + order.getStatus());
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());

        if (request.isSimulateSuccess()) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepo.save(order);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
        }

        return paymentRepo.save(payment);
    }

    @Override
    @Transactional
    public Payment getPaymentById(Long paymentId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("To'lov ma'lumoti topilmadi, ID: " + paymentId));

        if (currentUser.getRole() != Role.ADMIN && !payment.getOrder().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("To'lov ma'lumoti mavjud emas!");
        }
        return payment;
    }

    @Override
    @Transactional
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyurtma topilmadi, ID: " + orderId));

        if (currentUser.getRole() != Role.ADMIN && !order.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("To'lov ma'lumotlari mavjud emas!");
        }
        return paymentRepo.findByOrderIdOrderByCreatedAtDesc(orderId);
    }
}
