package uz.pdp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.dto.request.CartItemRequest;
import uz.pdp.dto.response.ApiResponse;
import uz.pdp.dto.response.CartResponse;
import uz.pdp.service.CartService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        return ResponseEntity.ok(ApiResponse.success("Sizning savatingiz", cartService.getMyCart()));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Mahsulot savatga qo'shildi", cartService.addItemToCart(request)));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(ApiResponse.success("Mahsulot soni yangilandi",
                cartService.updateItemQuantity(itemId, body.get("quantity"))));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCart(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success("Mahsulot savatdan o'chirildi", cartService.removeItemFromCart(itemId)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearMyCart() {
        cartService.clearMyCart();
        return ResponseEntity.ok(ApiResponse.success("Savat tozalandi"));
    }
}
