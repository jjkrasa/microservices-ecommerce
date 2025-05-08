package com.ecommerce.cartservice.cart_service.service;

import com.ecommerce.cartservice.cart_service.client.ProductClient;
import com.ecommerce.cartservice.cart_service.dto.*;
import com.ecommerce.cartservice.cart_service.exception.ErrorCode;
import com.ecommerce.cartservice.cart_service.exception.NotFoundException;
import com.ecommerce.cartservice.cart_service.model.Cart;
import com.ecommerce.cartservice.cart_service.model.CartItem;
import com.ecommerce.cartservice.cart_service.repository.CartRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final ProductClient productClient;

    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCartByUserId(userId);

        List<Long> productIds = cart
                .getItems()
                .stream()
                .map(CartItem::getProductId)
                .toList();

        List<ProductResponse> productsByIds = productClient.getProductsByIds(productIds);

        Map<Long, ProductResponse> productMap = productsByIds
                .stream()
                .collect(Collectors.toMap(ProductResponse::id, Function.identity()));

        List<CartItemResponse> cartItemResponse = cart
                .getItems()
                .stream()
                .map(
                        item -> CartItemResponse.builder()
                                .id(item.getId())
                                .quantity(item.getQuantity())
                                .product(productMap.get(item.getProductId()))
                                .build()
                )
                .toList();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(cartItemResponse)
                .build();
    }

    @Transactional
    public void addItemToCart(Long userId, AddCartItemRequest request) {
        productClient.getProductById(request.getProductId());

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> createNewCart(userId));

        cart
                .getItems()
                .stream()
                .filter(cartItem -> cartItem.getProductId().equals(request.getProductId()))
                .findFirst()
                .ifPresentOrElse(
                        cartItem -> cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity()),
                        () -> cart.getItems().add(
                                CartItem.builder()
                                        .cart(cart)
                                        .productId(request.getProductId())
                                        .quantity(request.getQuantity())
                                        .build())
                );

        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserIdOrThrow(userId);

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public void updateCartItemQuantity(Long userId, Long cartItemId, @Valid UpdateCartItemRequest request) {
        Cart cart = getCartByUserIdOrThrow(userId);

        CartItem cartItem = cart
                .getItems()
                .stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_WAS_NOT_FOUND_IN_CART.getMessage()));

        if (request.getQuantity() == 0) {
            cart.getItems().remove(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
        }

        cartRepository.save(cart);
    }

    @Transactional
    public void deleteCartItem(Long userId, Long cartItemId) {
        Cart cart = getCartByUserIdOrThrow(userId);

        boolean isRemoved = cart.getItems().removeIf(cartItem -> cartItem.getId().equals(cartItemId));

        if (!isRemoved) {
            throw new NotFoundException(ErrorCode.PRODUCT_WAS_NOT_FOUND_IN_CART.getMessage());
        }

        cartRepository.save(cart);
    }

    private Cart createNewCart(Long userId) {
        return Cart.builder().userId(userId).items(new ArrayList<>()).build();
    }

    private Cart getCartByUserIdOrThrow(Long userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(ErrorCode.CART_DOES_NOT_EXIST.getMessage()));
    }

    private Cart getOrCreateCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> cartRepository.save(createNewCart(userId)));
    }
}
