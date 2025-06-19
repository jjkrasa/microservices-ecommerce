package com.ecommerce.cartservice.cart_service.service;

import com.ecommerce.cartservice.cart_service.client.ProductClient;
import com.ecommerce.cartservice.cart_service.dto.*;
import com.ecommerce.cartservice.cart_service.model.Cart;
import com.ecommerce.cartservice.cart_service.model.CartItem;
import com.ecommerce.cartservice.cart_service.repository.CartRepository;
import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final ProductClient productClient;

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId, String sessionId) {
        Cart cart = getOrCreateCartByUserIdOrSessionId(userId, sessionId);

        List<CartItem> items = cart.getItems();

        if (items.isEmpty()) {
            return CartResponse.builder()
                    .id(cart.getId())
                    .userId(cart.getUserId())
                    .items(Collections.emptyList())
                    .build();
        }

        List<Long> productIds = items
                .stream()
                .map(CartItem::getProductId)
                .toList();

        List<ProductResponse> productsByIds = productClient.getProductsByIds(productIds);

        Map<Long, ProductResponse> productMap = productsByIds
                .stream()
                .collect(Collectors.toMap(ProductResponse::id, Function.identity()));

        List<CartItemResponse> cartItemResponse = items
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
    public void addItemToCart(Long userId, String sessionId, AddCartItemRequest request) {
        productClient.getProductById(request.getProductId());

        Cart cart = getOrCreateCartByUserIdOrSessionId(userId, sessionId);

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
    public void clearCart(Long userId, String sessionId) {
        Cart cart = getCartByUserIdOrSessionIdOrThrow(userId, sessionId);

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public void updateCartItemQuantity(Long userId, String sessionId, Long cartItemId, @Valid UpdateCartItemRequest request) {
        Cart cart = getCartByUserIdOrSessionIdOrThrow(userId, sessionId);

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
    public void deleteCartItem(Long userId, String sessionId, Long cartItemId) {
        Cart cart = getCartByUserIdOrSessionIdOrThrow(userId, sessionId);

        boolean isRemoved = cart.getItems().removeIf(cartItem -> cartItem.getId().equals(cartItemId));

        if (!isRemoved) {
            throw new NotFoundException(ErrorCode.PRODUCT_WAS_NOT_FOUND_IN_CART.getMessage());
        }

        cartRepository.save(cart);
    }

    private Cart createNewCart(Long userId, String sessionId) {
        return Cart.builder().
                userId(userId)
                .sessionId(sessionId)
                .items(new ArrayList<>())
                .build();
    }

    private Cart getCartByUserIdOrSessionIdOrThrow(Long userId, String sessionId) {
        if (userId != null) {
            return getCartByUserIdOrThrow(userId);
        } else if (sessionId != null) {
            return getCartBySessionIdOrThrow(sessionId);
        }

        throw new BadRequestException(ErrorCode.MISSING_USER_OR_SESSION.getMessage());
    }

    private Cart getCartByUserIdOrThrow(Long userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(ErrorCode.CART_DOES_NOT_EXIST.getMessage()));
    }

    private Cart getCartBySessionIdOrThrow(String sessionId) {
        return cartRepository.findBySessionId(sessionId).orElseThrow(() -> new NotFoundException(ErrorCode.CART_DOES_NOT_EXIST.getMessage()));
    }

    private Cart getOrCreateCartByUserIdOrSessionId(Long userId, String sessionId) {
        if (userId != null) {
            return cartRepository.findByUserId(userId).orElseGet(() -> createNewCart(userId, null));
        } else if (sessionId != null && !sessionId.isBlank()) {
            return cartRepository.findBySessionId(sessionId).orElseGet(() -> createNewCart(null, sessionId));
        }

        throw new BadRequestException(ErrorCode.MISSING_USER_OR_SESSION.getMessage());
    }
}
