package com.citadelcult.citadelcult.order;

import com.citadelcult.citadelcult.order.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByToken(String token);
}
