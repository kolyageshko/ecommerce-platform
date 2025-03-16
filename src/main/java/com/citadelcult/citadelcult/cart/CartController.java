package com.citadelcult.citadelcult.cart;

import com.citadelcult.citadelcult.cart.dtos.*;
import com.citadelcult.citadelcult.cart.entities.Cart;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @GetMapping("/{token}")
    public ResponseEntity<Cart> getCart(
            @PathVariable String token
    ) {
        var cart = cartService.findByTokenOrThrow(token);
        cart = cartService.getCartOrThrow(cart.getId());
        return ResponseEntity.ok(cart);
    }

    @PostMapping()
    public ResponseEntity<Cart> createCart(@RequestBody(required = false) CreateCartDTO createCartDTO, HttpServletRequest request) {
        var cart = (createCartDTO != null) ? cartService.createCart(createCartDTO, request) : cartService.createCart(request);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/{token}")
    public ResponseEntity<Cart> updateCart(
            @PathVariable String token,
            @RequestBody UpdateCartDTO updateCartDTO
    ) {
        var cart = cartService.findByTokenOrThrow(token);
        cart = cartService.updateCart(cart.getId(), updateCartDTO);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{token}/line-items")
    public ResponseEntity<Cart> addLineItem(
            @PathVariable String token,
            @RequestBody AddLineItemDTO addLineItemDTO,
            HttpServletRequest request
    ) {
        var cart = cartService.findByTokenOrThrow(token);
        cart = cartService.addLineItem(cart.getId(), addLineItemDTO, request);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/{token}/line-items/{lineId}")
    public ResponseEntity<Cart> updateLineItem(
            @PathVariable String token,
            @PathVariable Long lineId,
            @RequestBody UpdateLineItemDTO updateLineItemDTO
    ) {
        var cart = cartService.findByTokenOrThrow(token);
        cart = cartService.updateLineItem(cart.getId(), lineId, updateLineItemDTO);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{token}/line-items/{lineId}")
    public ResponseEntity<Cart> deleteLineItem(
            @PathVariable String token,
            @PathVariable Long lineId
    ) {
        var cart = cartService.findByTokenOrThrow(token);
        cart = cartService.deleteLineItem(cart.getId(), lineId);
        return ResponseEntity.ok(cart);
    }
}
