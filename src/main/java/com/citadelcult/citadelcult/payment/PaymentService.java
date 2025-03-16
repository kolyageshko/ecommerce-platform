package com.citadelcult.citadelcult.payment;

import com.citadelcult.citadelcult.exceptions.ResourceNotFoundException;
import com.citadelcult.citadelcult.payment.entities.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentsRepository paymentsRepository;

    public PaymentProvider findPaymentProviderById(String paymentProviderId) {
        return paymentsRepository.findById(paymentProviderId).orElse(null);
    }

    public PaymentProvider findPaymentProviderByIdOrThrow(String paymentProviderId) {
        return paymentsRepository.findById(paymentProviderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment provider not found with id: " + paymentProviderId));
    }
}
