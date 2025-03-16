package com.citadelcult.citadelcult.checkout;

import com.citadelcult.citadelcult.cart.CartService;
import com.citadelcult.citadelcult.order.entities.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkouts")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final CartService cartService;

    @PostMapping("/{token}/orders")
    public ResponseEntity<Order> checkout(@PathVariable String token) {
        var cart = cartService.findByTokenOrThrow(token);
        var order = checkoutService.checkout(cart.getId());
        return ResponseEntity.ok(order);
    }
}
