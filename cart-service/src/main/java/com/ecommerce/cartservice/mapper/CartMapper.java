package com.ecommerce.cartservice.mapper;

import com.ecommerce.cartservice.dto.AddCartItemRequest;
import com.ecommerce.cartservice.dto.CartItemResponse;
import com.ecommerce.cartservice.dto.CartResponse;
import com.ecommerce.cartservice.dto.ProductResponse;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "id", source = "cart.id")
    @Mapping(target = "userId", source = "cart.userId")
    @Mapping(target = "items", expression = "java(mapCartItems(cart.getItems(), productsByIds))")
    CartResponse cartToCartResponse(Cart cart, List<ProductResponse> productsByIds);

    default List<CartItemResponse> mapCartItems(List<CartItem> cartItems, List<ProductResponse> productsByIds) {
        if (cartItems == null || cartItems.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, ProductResponse> productMap = productsByIds
                .stream()
                .collect(Collectors.toMap(ProductResponse::id, Function.identity()));

        return cartItems
                .stream()
                .map(cartItem -> cartItemToCartItemResponse(cartItem, productMap.get(cartItem.getProductId())))
                .toList();
    }

    @Mapping(target = "id", source = "cartItem.id")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "quantity", source = "cartItem.quantity")
    CartItemResponse cartItemToCartItemResponse(CartItem cartItem, ProductResponse product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", source = "cart")
    @Mapping(target = "productId", source = "request.productId")
    @Mapping(target = "quantity", source = "request.quantity")
    CartItem addCartItemRequestToCartItem(Cart cart, AddCartItemRequest request);
}
