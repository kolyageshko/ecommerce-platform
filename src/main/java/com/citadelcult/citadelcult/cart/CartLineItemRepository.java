package com.citadelcult.citadelcult.cart;

import com.citadelcult.citadelcult.cart.entities.CartLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartLineItemRepository extends JpaRepository<CartLineItem, Long> {
}
