package com.citadelcult.citadelcult.currency;

import com.citadelcult.citadelcult.currency.entities.Currency;
import com.citadelcult.citadelcult.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrenciesService {

    private final CurrenciesRepository currenciesRepository;

    public List<Currency> getAllCurrencies() {
        return currenciesRepository.findAll();
    }

    public Currency getByCode(String code) {
        return currenciesRepository.findByCode(code);
    }

    public Currency getByCodeOrThrow(String currencyCode) {
        Currency currency = getByCode(currencyCode);
        if (currency == null) {
            throw new ResourceNotFoundException("Currency not found with code: " + currencyCode);
        }
        return currency;
    }
}
