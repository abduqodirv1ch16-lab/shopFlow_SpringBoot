package uz.pdp.mapper;

import org.springframework.stereotype.Component;
import uz.pdp.dto.response.OrderResponse;
import uz.pdp.entity.Address;
import uz.pdp.entity.Order;
import uz.pdp.entity.OrderItem;

import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order) {
        if (order == null) return null;
        List<OrderResponse.OrderItemDto> itemDtos = order.getItems().stream()
                .map(this::toOrderItemDto)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                formatAddress(order.getAddress()),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                itemDtos
        );
    }

    private OrderResponse.OrderItemDto toOrderItemDto(OrderItem item) {
        return new OrderResponse.OrderItemDto(
                item.getProduct().getId(),
                item.getProductName(),
                item.getProductPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }

    private String formatAddress(Address address) {
        if (address == null) return "";
        String base = address.getCity() + ", " + address.getStreet() + ", " + address.getHouseNumber();
        if (address.getAdditionalInfo() != null && !address.getAdditionalInfo().isBlank()) {
            base += " (" + address.getAdditionalInfo() + ")";
        }
        return base;
    }
}
