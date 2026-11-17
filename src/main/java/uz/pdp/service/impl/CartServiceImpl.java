package uz.pdp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.dto.request.CartItemRequest;
import uz.pdp.dto.response.CartResponse;
import uz.pdp.entity.Cart;
import uz.pdp.entity.CartItems;
import uz.pdp.entity.Product;
import uz.pdp.entity.User;
import uz.pdp.exception.BadRequestException;
import uz.pdp.exception.ResourceNotFoundException;
import uz.pdp.mapper.CartMapper;
import uz.pdp.repo.CartItemRepo;
import uz.pdp.repo.CartRepo;
import uz.pdp.repo.ProductRepo;
import uz.pdp.service.CartService;
import uz.pdp.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final UserService userService;
    private final CartRepo cartRepo;
    private final CartMapper cartMapper;
    private final ProductRepo productRepo;
    private final CartItemRepo cartItemRepo;

    private Cart getOrCreateCurrentUserCart() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return cartRepo.findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(currentUser);
                    return cartRepo.save(newCart);
                });
    }

    @Override
    @Transactional
    public CartResponse getMyCart() {
        Cart cart = getOrCreateCurrentUserCart();
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(CartItemRequest request) {
        Cart cart = getOrCreateCurrentUserCart();
        Product product = productRepo.findByIdAndActiveTrue(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Mahsulot topilmadi yoki sotuvda emas, ID: " + request.getProductId()));

        Optional<CartItems> existingItemOpt = cartItemRepo.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingItemOpt.isPresent()) {
            CartItems existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > product.getQuantity()) {
                throw new BadRequestException("Omborda yetarli mahsulot yo'q! Mavjud qoldiq: " + product.getQuantity());
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepo.save(existingItem);
        } else {
            if (request.getQuantity() > product.getQuantity()) {
                throw new BadRequestException("Omborda yetarli mahsulot yo'q! Mavjud qoldiq: " + product.getQuantity());
            }
            CartItems newItem = new CartItems();
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.addItem(newItem);
        }

        return cartMapper.toResponse(cartRepo.save(cart));
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long cartItemId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new BadRequestException("Mahsulot soni kamida 1 ta bo'lishi kerak");
        }
        Cart cart = getOrCreateCurrentUserCart();
        CartItems cartItems = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Savat elementi topilmadi, ID: " + cartItemId));

        if (!cartItems.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Bu element sizning savatingizga tegishli emas!");
        }

        Product product = cartItems.getProduct();
        if (quantity > product.getQuantity()) {
            throw new BadRequestException("Omborda yetarli mahsulot yo'q! Mavjud qoldiq: " + product.getQuantity());
        }

        cartItems.setQuantity(quantity);
        cartItemRepo.save(cartItems);

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long cartItemId) {
        Cart cart = getOrCreateCurrentUserCart();
        CartItems cartItems = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Savat elementi topilmadi, ID: " + cartItemId));

        if (!cartItems.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Bu element sizning savatingizga tegishli emas!");
        }

        cart.removeItem(cartItems);
        return cartMapper.toResponse(cartRepo.save(cart));
    }

    @Override
    @Transactional
    public void clearMyCart() {
        Cart cart = getOrCreateCurrentUserCart();
        cart.getItems().clear();
        cartRepo.save(cart);
    }
}
