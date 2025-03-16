package com.citadelcult.citadelcult.payment;

import com.citadelcult.citadelcult.market.entities.Market;
import com.citadelcult.citadelcult.payment.entities.PaymentProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentsRepository extends JpaRepository<PaymentProvider, String> {
}
