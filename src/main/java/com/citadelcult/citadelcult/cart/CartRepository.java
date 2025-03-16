package com.citadelcult.citadelcult.cart;

import com.citadelcult.citadelcult.cart.entities.Cart;
import com.citadelcult.citadelcult.order.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByToken(String token);
}
