package com.cartService.service;

import com.cartService.dto.*;
import com.cartService.exception.CartNotFoundException;
import com.cartService.exception.NoProductFound;
import com.cartService.feignClient.ProductFeignClient;
import com.cartService.model.Cart;
import com.cartService.model.CartItem;
import com.cartService.repo.CartRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository repo;
    private final ProductFeignClient feignClient;


    @PreAuthorize("hasRole('USER')")
    public CartResponse addToCart(AddItemRequest request, String userId) {
        Cart cart = repo.findByUserId(userId).orElseGet(() -> createNewCart(userId));

        Optional<CartItem> alreadyExist = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (alreadyExist.isPresent()) {
            CartItem existItem = alreadyExist.get();
            existItem.setQuantity(request.getQuantity() + existItem.getQuantity());
        } else {
            ProductResponse response;
            try {
                response = feignClient.getProduct(request.getProductId());
            }
            catch (FeignException e){
                throw e;
            }
            CartItem cartItem = CartItem.builder()
                    .productId(request.getProductId())
                    .sku(response.getSku())
                    .quantity(request.getQuantity())
                    .price(response.getPrice())
                    .productName(response.getProductName())
                    .build();
            //Adding new Item in a cart
            cart.getCartItems().add(cartItem);
        }
        cart.setTotalPrice(calculateTotalPrice(cart));

        return convertToCartResponse(repo.save(cart));
    }

    @PreAuthorize("hasRole('USER')")
    public CartResponse deleteCartItem(String productId, String userId) {
        Cart cart = repo.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart is Empty..!"));

        boolean removed = cart.getCartItems()
                .removeIf(item -> item.getProductId().equals(productId));

        if (!removed) {
            throw new NoProductFound("Product with ID: " + productId + " not found in the cart.");
        }

        cart.setTotalPrice(calculateTotalPrice(cart));
        repo.save(cart);
        return convertToCartResponse(cart);
    }

    @PreAuthorize("hasRole('USER')")
    public CartResponse getDetailsOfCart(String userId) {
        Cart cart = repo.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart is Empty..!"));
        return convertToCartResponse(cart);
    }

    @PreAuthorize("hasRole('USER')")
    public CartResponse updateItemQuantity(UpdateRequest request, String userID) {
        Cart cart = repo.findByUserId(userID).orElseThrow(() -> new CartNotFoundException("Cart is Empty..!"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst().orElseThrow(() -> new NoProductFound("No Such Product Found with ID " + request.getProductId()));

        cartItem.setQuantity(request.getQuantity());
        cart.setTotalPrice(calculateTotalPrice(cart));

        return convertToCartResponse(repo.save(cart));
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public CartResponse clearCart(String userId) {
        Cart cart = repo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        Cart savedCart = repo.save(cart);
        return convertToCartResponse(savedCart);
    }


    private BigDecimal calculateTotalPrice(Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Cart createNewCart(String userId) {
        return Cart.builder()
                .userId(userId)
                .cartItems(new ArrayList<>()) // Always initialize lists!
                .totalPrice(BigDecimal.ZERO)
                .build();
    }

    private CartResponse convertToCartResponse(Cart cart) {

        List<CartItemResponseDto> itemResponseDto = cart.getCartItems().stream()
                .map(this::itemToResponse).toList();

        return CartResponse.builder()
                .id(cart.getCartId())
                .userId(cart.getUserId())
                .items(itemResponseDto)
                .totalPrice(cart.getTotalPrice())
                .build();
    }

    private CartItemResponseDto itemToResponse(CartItem item) {
        return CartItemResponseDto.builder()
                .productId(item.getProductId())
                .sku(item.getSku())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .productName(item.getProductName())
                .build();
    }

}
