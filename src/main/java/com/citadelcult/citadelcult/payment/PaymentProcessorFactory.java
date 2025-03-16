package com.citadelcult.citadelcult.payment;

import com.citadelcult.citadelcult.payment.entities.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProcessorFactory {
    private final FondyPaymentProcessor fondyPaymentProcessor;
    private final ManualPaymentProcessor manualPaymentProcessor;

    public PaymentProcessor createPaymentProcessor(PaymentProvider paymentMethod) {
        return switch (paymentMethod.getId().toUpperCase()) {
            case "FONDY" -> fondyPaymentProcessor;
            case "MANUAL" -> manualPaymentProcessor;
            default -> throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod.getId());
        };
    }
}