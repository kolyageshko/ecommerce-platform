package com.citadelcult.citadelcult.market;

import com.citadelcult.citadelcult.country.CountriesService;
import com.citadelcult.citadelcult.country.dtos.CountryDTO;
import com.citadelcult.citadelcult.currency.CurrenciesService;
import com.citadelcult.citadelcult.market.dtos.CreateMarketDTO;
import com.citadelcult.citadelcult.exceptions.ResourceNotFoundException;
import com.citadelcult.citadelcult.country.entities.Country;
import com.citadelcult.citadelcult.market.entities.Market;
import com.citadelcult.citadelcult.country.CountriesRepository;
import com.citadelcult.citadelcult.payment.PaymentsRepository;
import com.citadelcult.citadelcult.payment.entities.PaymentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketsService {

    private final MarketsRepository marketsRepository;
    private final CurrenciesService currenciesService;
    private final CountriesService countriesService;
    private final CountriesRepository countriesRepository;
    private final PaymentsRepository paymentsRepository;

    public List<Market> getAllMarkets() {
        return marketsRepository.findAll();
    }

    public Market getDefaultMarket() {
        return marketsRepository.findByCountriesIso("US")
                .orElseThrow(() -> new ResourceNotFoundException("Default market not found"));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Market createMarket(CreateMarketDTO marketDTO) {
        var market = new Market();
        market.setName(marketDTO.getName());

        market.setCurrency(currenciesService.getByCodeOrThrow(marketDTO.getCurrencyCode()));

        List<Country> countries = marketDTO.getCountries().stream()
                .map(countriesService::getByIsoOrThrow)
                .collect(Collectors.toList());

        market.setCountries(countries);

        if (marketDTO.getPaymentProviders() != null && !marketDTO.getPaymentProviders().isEmpty()) {
            List<PaymentProvider> paymentProviders = marketDTO.getPaymentProviders().stream()
                    .map(paymentProviderName -> paymentsRepository.findById(paymentProviderName)
                            .orElseThrow(() -> new ResourceNotFoundException("Payment provider not found with name: " + paymentProviderName)))
                    .collect(Collectors.toList());
            market.setPaymentProviders(paymentProviders);
        }

        market = marketsRepository.save(market);

        final Market finalMarket = market;

        countries.forEach(country -> {
            country.setMarket(finalMarket);
            countriesService.createCountry(country);
        });

        return market;
    }

    public Market getMarketByIdOrThrow(Long marketId) {
        if (marketId == null) {
            throw new ResourceNotFoundException("Market id must not be null");
        }
        return marketsRepository.findById(marketId)
                .orElseThrow(() -> new ResourceNotFoundException("Market not found with id: " + marketId));
    }


    public Optional<Market> getMarketByCountryCode(String countryCode) {
        return marketsRepository.findByCountriesIso(countryCode);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Market deleteMarket(Long id) {
        var market = getMarketByIdOrThrow(id);

        market.getCountries().forEach(country -> country.setMarket(null));
        countriesRepository.saveAll(market.getCountries());

        marketsRepository.deleteById(id);

        return market;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Market addCountry(Long marketId, CountryDTO countryDTO) {
        var market = getMarketByIdOrThrow(marketId);
        Country country = countriesService.getByIsoOrThrow(countryDTO.getCountryCode());

        country.setMarket(market);
        countriesRepository.save(country);

        market.getCountries().add(country);
        return marketsRepository.save(market);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Market removeCountry(Long marketId, String countryCode) {
        var market = getMarketByIdOrThrow(marketId);

        Country countryToRemove = countriesService.getByIsoOrThrow(countryCode);
        market.getCountries().remove(countryToRemove);

        countryToRemove.setMarket(null);
        countriesRepository.save(countryToRemove);

        return marketsRepository.save(market);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Market addPaymentProviderToMarket(Long marketId, String paymentProviderId) {
        var market = getMarketByIdOrThrow(marketId);
        var paymentProvider = paymentsRepository.findById(paymentProviderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment provider not found with id: " + paymentProviderId));

        if (market.getPaymentProviders().contains(paymentProvider)) {
            String errorMessage = "Payment provider with id " + paymentProviderId + " is already associated with market " + marketId;
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        market.getPaymentProviders().add(paymentProvider);

        marketsRepository.save(market);

        return market;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Market removePaymentProviderFromMarket(Long marketId, String paymentProviderId) {
        var market = getMarketByIdOrThrow(marketId);
        var paymentProvider = paymentsRepository.findById(paymentProviderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment provider not found with id: " + paymentProviderId));

        if (!market.getPaymentProviders().contains(paymentProvider)) {
            String errorMessage = "Payment provider with id " + paymentProviderId + " is not associated with market " + marketId;
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        market.getPaymentProviders().remove(paymentProvider);

        marketsRepository.save(market);
        paymentsRepository.save(paymentProvider);

        return market;
    }
}
