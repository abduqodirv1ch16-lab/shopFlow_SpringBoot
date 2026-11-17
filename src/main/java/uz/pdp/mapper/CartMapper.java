package uz.pdp.mapper;

import org.springframework.stereotype.Component;
import uz.pdp.dto.response.CartResponse;
import uz.pdp.entity.Cart;
import uz.pdp.entity.CartItems;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {
    public CartResponse toResponse(Cart cart) {
        if (cart == null) return null;
        List<CartResponse.CartItemDto> itemDtos = cart.getItems().stream()
                .map(this::toCartItemDto)
                .toList();
        BigDecimal totalPrice = itemDtos.stream()
                .map(CartResponse.CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), itemDtos, totalPrice);
    }

    private CartResponse.CartItemDto toCartItemDto(CartItems items) {
        BigDecimal unitPrice = items.getProduct().getPrice();
        BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(items.getQuantity()));
        return new CartResponse.CartItemDto(
                items.getId(),
                items.getProduct().getId(),
                items.getProduct().getName(),
                unitPrice,
                items.getQuantity(),
                subTotal
        );
    }
}
