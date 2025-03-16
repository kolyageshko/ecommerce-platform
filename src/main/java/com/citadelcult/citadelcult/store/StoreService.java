package com.citadelcult.citadelcult.store;

import com.citadelcult.citadelcult.currency.entities.Currency;
import com.citadelcult.citadelcult.store.entities.StoreSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    public StoreSettings create(StoreSettings storeSettings) {
        return storeRepository.save(storeSettings);
    }

    public StoreSettings getStoreSettings() {
        return storeRepository.findById("citadelcult").orElse(null);
    }

    public List<Currency> getStoreCurrencies() {
        StoreSettings storeSettings = getStoreSettings();
        return storeSettings != null ? storeSettings.getCurrencies() : null;
    }
}
