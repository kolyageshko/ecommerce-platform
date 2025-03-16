package com.citadelcult.citadelcult.store;

import com.citadelcult.citadelcult.store.entities.StoreSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<StoreSettings> getStoreSettings() {
        StoreSettings storeSettings = storeService.getStoreSettings();

        if (storeSettings != null) {
            return ResponseEntity.ok(storeSettings);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
