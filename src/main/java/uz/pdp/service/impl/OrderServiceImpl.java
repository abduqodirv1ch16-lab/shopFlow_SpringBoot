package uz.pdp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.dto.request.OrderRequest;
import uz.pdp.dto.response.OrderResponse;
import uz.pdp.entity.*;
import uz.pdp.enums.OrderStatus;
import uz.pdp.enums.Role;
import uz.pdp.exception.BadRequestException;
import uz.pdp.exception.ResourceNotFoundException;
import uz.pdp.mapper.OrderMapper;
import uz.pdp.repo.*;
import uz.pdp.service.OrderService;
import uz.pdp.service.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserService userService;
    private final CartRepo cartRepo;
    private final AddressRepo addressRepo;
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final OrderMapper orderMapper;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Cart cart = cartRepo.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BadRequestException("Savatingiz topilmadi"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Savat bo'sh! Buyurtma berish uchun avval mahsulot qo'shing");
        }

        Address address = new Address();
        address.setUser(currentUser);
        address.setCity(request.getCity().trim());
        address.setStreet(request.getStreet().trim());
        address.setHouseNumber(request.getHouseNumber().trim());
        address.setAdditionalInfo(request.getAdditionalInfo());

        Address savedAddress = addressRepo.save(address);

        Order order = new Order();
        order.setUser(currentUser);
        order.setAddress(savedAddress);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItems item : cart.getItems()) {
            Product product = item.getProduct();
            if (!product.isActive()) {
                throw new BadRequestException("Mahsulot sotuvdan olingan: " + product.getName());
            }
            if (product.getQuantity() < item.getQuantity()) {
                throw new BadRequestException("Omborda yetarli mahsulot yo'q: " + product.getName()
                        + ". Mavjud: " + product.getQuantity());
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepo.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(item.getQuantity());

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepo.save(order);

        cart.getItems().clear();
        cartRepo.save(cart);

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public Page<OrderResponse> getMyOrder(Pageable pageable) {
        User user = userService.getCurrentAuthenticatedUser();
        return orderRepo.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse getOrderById(Long orderId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyurtma topilmadi, ID: " + orderId));

        if (currentUser.getRole() != Role.ADMIN && !order.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Siz boshqa foydalanuvchining buyurtmasini ko'ra olmaysiz!");
        }
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelMyOrder(Long orderId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Order order = orderRepo.findByIdAndUserId(orderId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyurtma topilmadi"));

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Buyurtma allaqachon jo'natilgan, uni bekor qilib bo'lmaydi!");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Buyurtma allaqachon bekor qilingan!");
        }

        restoreStock(order);
        order.setStatus(OrderStatus.CANCELLED);
        return orderMapper.toResponse(orderRepo.save(order));
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepo.save(product);
        }
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable).map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyurtma topilmadi, ID: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Bekor qilingan buyurtma holatini o'zgartirib bo'lmaydi!");
        }

        order.setStatus(newStatus);
        return orderMapper.toResponse(orderRepo.save(order));
    }

    @Override
    @Transactional
    public Map<String, Object> getAdminStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUser", userRepo.count());
        stats.put("totalProducts", productRepo.count());
        stats.put("totalOrders", orderRepo.count());
        stats.put("deliveredOrders", orderRepo.countByStatus(OrderStatus.DELIVERED));
        stats.put("cancelledOrders", orderRepo.countByStatus(OrderStatus.CANCELLED));
        stats.put("totalRevenue", orderRepo.sumTotalAmountByStatus(OrderStatus.DELIVERED));
        return stats;
    }

    @Override
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        List<Object[]> rows = orderRepo.findTopSellingProducts(
                OrderStatus.CANCELLED,
                PageRequest.of(0, limit)
        );
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("productId", row[0]);
            item.put("productName", row[1]);
            item.put("totalSoldQuantity", row[2]);
            result.add(item);
        }
        return result;
    }
}
