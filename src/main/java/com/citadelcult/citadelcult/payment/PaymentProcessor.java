package com.citadelcult.citadelcult.payment;

import com.citadelcult.citadelcult.order.entities.Order;

public interface PaymentProcessor {
    String getPaymentUrl(Order order);
}
