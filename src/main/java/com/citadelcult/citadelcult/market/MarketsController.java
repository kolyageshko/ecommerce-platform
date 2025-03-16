package com.citadelcult.citadelcult.market;

import com.citadelcult.citadelcult.country.dtos.CountryDTO;
import com.citadelcult.citadelcult.market.dtos.CreateMarketDTO;
import com.citadelcult.citadelcult.market.entities.Market;
import com.citadelcult.citadelcult.payment.dtos.PaymentProviderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
public class MarketsController {

    private final MarketsService marketsService;

    @GetMapping
    public ResponseEntity<List<Market>> getAllMarkets() {
        List<Market> markets = marketsService.getAllMarkets();
        return ResponseEntity.ok(markets);
    }

    @PostMapping
    public ResponseEntity<Market> createMarket(@RequestBody CreateMarketDTO marketDTO) {
        return ResponseEntity.ok(marketsService.createMarket(marketDTO));
    }

    @PostMapping("/{marketId}/payment-providers")
    public Market addPaymentProviderToMarket(
            @PathVariable Long marketId,
            @RequestBody PaymentProviderDTO paymentProviderDTO
            ) {
        return marketsService.addPaymentProviderToMarket(marketId, paymentProviderDTO.getId());
    }

    @DeleteMapping("/{marketId}/payment-providers")
    public Market removePaymentProviderFromMarket(
            @PathVariable Long marketId,
            @RequestBody PaymentProviderDTO paymentProviderDTO
    ) {
        return marketsService.removePaymentProviderFromMarket(marketId, paymentProviderDTO.getId());
    }

    @PostMapping("/{marketId}/countries")
    public ResponseEntity<Market> addCountry(
            @PathVariable Long marketId,
            @RequestBody CountryDTO countryDTO
    ) {
        Market updatedMarket = marketsService.addCountry(marketId, countryDTO);
        return ResponseEntity.ok(updatedMarket);
    }

    @DeleteMapping("/{marketId}/countries")
    public ResponseEntity<Market> removeCountry(@PathVariable Long marketId, @RequestBody CountryDTO countryDTO) {
        Market updatedMarket = marketsService.removeCountry(marketId, countryDTO.getCountryCode());
        return ResponseEntity.ok(updatedMarket);
    }

    @DeleteMapping("/{marketId}")
    public ResponseEntity<Market> deleteMarket(@PathVariable Long marketId) {
        Market deletedMarket = marketsService.deleteMarket(marketId);

        if (deletedMarket != null) {
            return new ResponseEntity<>(deletedMarket, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
