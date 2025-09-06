package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.client.ProductClient;
import com.ecommerce.cartservice.dto.*;
import com.ecommerce.cartservice.mapper.CartMapper;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.repository.CartRepository;
import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private CartMapper cartMapper;

    private Cart userCart;

    private Cart anonymousCart;

    private CartResponse userCartResponse;

    private CartResponse anonymousCartResponse;

    private List<ProductResponse> productsResponse;


    @BeforeEach
    void setUp() {
        CartItem userCartItem = CartItem.builder()
                .id(1L)
                .cart(null)
                .productId(1L)
                .quantity(1)
                .build();

        CartItem anonymousCartItem = CartItem.builder()
                .id(2L)
                .cart(null)
                .productId(1L)
                .quantity(1)
                .build();

        userCart = Cart.builder()
                .id(1L)
                .userId(1L)
                .sessionId(null)
                .items(new ArrayList<>(List.of(userCartItem)))
                .build();

        anonymousCart = Cart.builder()
                .id(2L)
                .userId(null)
                .sessionId("SessionId")
                .items(new ArrayList<>(List.of(anonymousCartItem)))
                .build();

        userCartItem.setCart(userCart);
        anonymousCartItem.setCart(anonymousCart);


        ProductResponse productResponse = new ProductResponse(
                1L,
                "Product",
                "Category",
                "Description",
                BigDecimal.valueOf(100),
                10,
                "ImageUrl"
        );

        productsResponse = List.of(productResponse);

        CartItemResponse userCartItemResponse = CartItemResponse.builder()
                .id(1L)
                .product(productResponse)
                .quantity(1)
                .build();

        CartItemResponse anonymousCartItemResponse = CartItemResponse.builder()
                .id(2L)
                .product(productResponse)
                .quantity(1)
                .build();

        userCartResponse = CartResponse.builder()
                .id(1L)
                .userId(1L)
                .items(new ArrayList<>(List.of(userCartItemResponse)))
                .build();

        anonymousCartResponse = CartResponse.builder()
                .id(2L)
                .userId(null)
                .items(new ArrayList<>(List.of(anonymousCartItemResponse)))
                .build();
    }

    @Nested
    @DisplayName("getCart() tests")
    class GetCart {

        @Test
        void getCart_shouldReturnUserCartResponse_whenCartDoesntExist() {
            userCartResponse.getItems().clear();

            when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
            when(cartMapper.cartToCartResponse(any(Cart.class), eq(Collections.emptyList()))).thenReturn(userCartResponse);

            CartResponse response = cartService.getCart(1L, null);

            assertThat(response).isNotNull();
            assertThat(response.getItems()).isEmpty();
            verify(cartRepository, times(1)).findByUserId(1L);
            verify(cartMapper, times(1)).cartToCartResponse(any(Cart.class), eq(Collections.emptyList()));
            verifyNoMoreInteractions(productClient, cartMapper);
        }

        @Test
        void getCart_shouldReturnUserCartResponse_whenCartIsEmpty() {
            userCart.getItems().clear();

            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));
            when(cartMapper.cartToCartResponse(userCart, Collections.emptyList())).thenReturn(userCartResponse);

            CartResponse response = cartService.getCart(1L, null);

            assertThat(response).isNotNull();
            verify(cartRepository, times(1)).findByUserId(1L);
            verify(cartMapper, times(1)).cartToCartResponse(userCart, Collections.emptyList());
            verifyNoMoreInteractions(productClient, cartMapper);
        }

        @Test
        void getCart_shouldReturnAnonymousCartResponse_whenCartIsEmpty() {
            anonymousCart.getItems().clear();

            when(cartRepository.findBySessionId("SessionId")).thenReturn(Optional.of(anonymousCart));
            when(cartMapper.cartToCartResponse(anonymousCart, Collections.emptyList())).thenReturn(anonymousCartResponse);

            CartResponse response = cartService.getCart(null, "SessionId");

            assertThat(response).isNotNull();
            verify(cartRepository, times(1)).findBySessionId("SessionId");
            verify(cartMapper, times(1)).cartToCartResponse(anonymousCart, Collections.emptyList());
            verifyNoMoreInteractions(productClient, cartMapper);
        }

        @Test
        void getCart_shouldThrowBadRequest_whenSessionIdIsBlank() {
            assertThrows(BadRequestException.class, () -> cartService.getCart(null, ""));
            verify(cartRepository, never()).findBySessionId(anyString());
            verify(cartRepository, never()).findByUserId(anyLong());
        }

        @Test
        void getCart_shouldThrowBadRequest_whenUserIdAndSessionIdAreNull() {
            assertThrows(BadRequestException.class, () -> cartService.getCart(null, null));
            verify(cartRepository, never()).findBySessionId(anyString());
            verify(cartRepository, never()).findByUserId(anyLong());
        }

        @Test
        void getCart_shouldReturnUserCartResponse() {
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));
            when(productClient.getProductsByIds(List.of(1L))).thenReturn(productsResponse);
            when(cartMapper.cartToCartResponse(userCart, productsResponse)).thenReturn(userCartResponse);

            CartResponse response = cartService.getCart(1L, null);

            assertThat(response).isNotNull();
            assertThat(response.getItems()).hasSize(1);
            verify(cartRepository, times(1)).findByUserId(1L);
            verify(productClient, times(1)).getProductsByIds(List.of(1L));
            verify(cartMapper, times(1)).cartToCartResponse(userCart, productsResponse);
        }
    }

    @Nested
    @DisplayName("addItemToCart() tests")
    class AddItemToCart {
        @Test
        void addItemToCart_shouldIncreaseItemQuantity_whenItemIsInCartAlready() {
            AddCartItemRequest req = new AddCartItemRequest();
            req.setProductId(1L);
            req.setQuantity(2);


            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));

            cartService.addItemToCart(1L, null, req);

            assertThat(userCart.getItems()).hasSize(1);
            assertEquals(3, userCart.getItems().getFirst().getQuantity());
            verify(productClient, times(1)).getProductById(req.getProductId());
            verify(cartRepository, times(1)).findByUserId(1L);
            verify(cartRepository, times(1)).save(userCart);
        }

        @Test
        void addItemToCart_shouldAddNewItem() {
            AddCartItemRequest req = new AddCartItemRequest();
            req.setProductId(2L);
            req.setQuantity(2);

            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));
            when(cartMapper.addCartItemRequestToCartItem(userCart, req)).thenReturn(new CartItem(2L, userCart, 2L, 2));

            cartService.addItemToCart(1L, null, req);

            assertThat(userCart.getItems()).hasSize(2);
            verify(productClient, times(1)).getProductById(req.getProductId());
            verify(cartRepository, times(1)).findByUserId(1L);
            verify(cartMapper, times(1)).addCartItemRequestToCartItem(userCart, req);
            verify(cartRepository, times(1)).save(userCart);
        }
    }

    @Nested
    @DisplayName("clearCart() tests")
    class ClearCart {
        @Test
        void clearCart_shouldThrowBadRequest() {
            assertThrows(BadRequestException.class, () -> cartService.clearCart(null, null));
            verify(cartRepository, never()).findBySessionId(anyString());
            verify(cartRepository, never()).findByUserId(anyLong());
        }

        @Test
        void clearCart_shouldRemoveAllAnonymousCartItems() {
            when(cartRepository.findBySessionId("SessionId")).thenReturn(Optional.of(anonymousCart));

            cartService.clearCart(null, "SessionId");

            assertThat(anonymousCart.getItems()).isEmpty();
            verify(cartRepository, times(1)).save(anonymousCart);
        }


        @Test
        void clearCart_shouldRemoveAllUserCartItems() {
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));

            cartService.clearCart(1L, null);

            assertThat(userCart.getItems()).isEmpty();
            verify(cartRepository, times(1)).save(userCart);
        }

        @Test
        void clearCart_shouldNotThrowException_whenNoItemsInCart() {
            userCart.getItems().clear();
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));

            cartService.clearCart(1L, null);

            assertThat(userCart.getItems()).isEmpty();
            verify(cartRepository, times(1)).save(userCart);
        }
    }

    @Nested
    @DisplayName("updateCartItemQuantity() tests")
    class UpdateCartItemQuantity {
        @Test
        void updateCartItemQuantity_shouldThrowNotFound() {
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));
            UpdateCartItemRequest req = new UpdateCartItemRequest();
            req.setQuantity(5);


            assertThrows(NotFoundException.class, () -> cartService.updateCartItemQuantity(1L, null, 10L, req));

            verify(cartRepository, times(1)).findByUserId(1L);
        }

        @Test
        void updateCartItemQuantity_shouldRemoveItemFromCart() {
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));
            UpdateCartItemRequest req = new UpdateCartItemRequest();
            req.setQuantity(0);

            cartService.updateCartItemQuantity(1L, null, 1L, req);


            assertThat(userCart.getItems()).isEmpty();
            verify(cartRepository, times(1)).findByUserId(1L);
            verify(cartRepository, times(1)).save(userCart);
        }

        @Test
        void updateCartItemQuantity_shouldUpdateItemQuantityInCart() {
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));
            UpdateCartItemRequest req = new UpdateCartItemRequest();
            req.setQuantity(2);

            cartService.updateCartItemQuantity(1L, null, 1L, req);

            assertEquals(2, userCart.getItems().getFirst().getQuantity());
            verify(cartRepository, times(1)).findByUserId(1L);
            verify(cartRepository, times(1)).save(userCart);
        }
    }

    @Nested
    @DisplayName("deleteCartItem() tests")
    class DeleteCartItem {
        @Test
        void deleteCartItem_shouldThrowNotFound() {
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));

            assertThrows(NotFoundException.class, () -> cartService.deleteCartItem(1L, null, 10L));
        }

        @Test
        void deleteCartItem_shouldRemoveItem() {
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(userCart));

            cartService.deleteCartItem(1L, null, 1L);

            assertThat(userCart.getItems()).isEmpty();
            verify(cartRepository, times(1)).save(userCart);
        }

        @Test
        void deleteCartItem_shouldThrowIfNotFound() {
            Cart cart = Cart.builder().userId(1L).items(new ArrayList<>()).build();
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
            assertThatThrownBy(() -> cartService.deleteCartItem(1L, null, 99L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(ErrorCode.PRODUCT_WAS_NOT_FOUND_IN_CART.getMessage());
        }
    }
}

