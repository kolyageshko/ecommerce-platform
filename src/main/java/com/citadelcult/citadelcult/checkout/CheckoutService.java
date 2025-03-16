package com.citadelcult.citadelcult.checkout;

import com.citadelcult.citadelcult.order.OrderService;
import com.citadelcult.citadelcult.order.entities.Order;
import com.citadelcult.citadelcult.payment.PaymentProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderService orderService;
    private final PaymentProcessorFactory paymentProcessorFactory;

    @Transactional
    public Order checkout(Long cartId) {
        var order = orderService.placeOrder(cartId);

        var paymentProcessor = paymentProcessorFactory.createPaymentProcessor(order.getPaymentProvider());
        var paymentUrl = paymentProcessor.getPaymentUrl(order);
        order.setPaymentUrl(paymentUrl);

        return order;
    }
}
