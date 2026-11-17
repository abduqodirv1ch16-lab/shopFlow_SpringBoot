package uz.pdp.service;

import uz.pdp.dto.request.CartItemRequest;
import uz.pdp.dto.response.CartResponse;

public interface CartService {
    CartResponse getMyCart();
    CartResponse addItemToCart(CartItemRequest request);
    CartResponse updateItemQuantity(Long cartItemId, Integer quantity);
    CartResponse removeItemFromCart(Long cartItemId);
    void clearMyCart();
}
