package com.citadelcult.citadelcult.currency;

import com.citadelcult.citadelcult.currency.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrenciesRepository extends JpaRepository<Currency, String> {
    Currency findByCode(String code);
}
