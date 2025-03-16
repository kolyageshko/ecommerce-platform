package com.citadelcult.citadelcult.currency;

import com.citadelcult.citadelcult.currency.entities.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class CurrenciesController {

    private final CurrenciesService currenciesService;

    @GetMapping
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        List<Currency> currencies = currenciesService.getAllCurrencies();
        return ResponseEntity.ok(currencies);
    }
}
