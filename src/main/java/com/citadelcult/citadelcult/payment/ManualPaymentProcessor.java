package com.citadelcult.citadelcult.payment;

import com.citadelcult.citadelcult.order.entities.Order;
import org.springframework.stereotype.Service;

@Service
public class ManualPaymentProcessor implements PaymentProcessor {


    @Override
    public String getPaymentUrl(Order order) {
        return null;
    }
}
