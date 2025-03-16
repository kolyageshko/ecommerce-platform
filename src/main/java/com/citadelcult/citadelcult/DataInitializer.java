package com.citadelcult.citadelcult;

import com.citadelcult.citadelcult.country.CountriesRepository;
import com.citadelcult.citadelcult.country.entities.Country;
import com.citadelcult.citadelcult.currency.CurrenciesRepository;
import com.citadelcult.citadelcult.currency.entities.Currency;
import com.citadelcult.citadelcult.exceptions.ResourceNotFoundException;
import com.citadelcult.citadelcult.market.MarketsRepository;
import com.citadelcult.citadelcult.market.entities.Market;
import com.citadelcult.citadelcult.media.MediaRepository;
import com.citadelcult.citadelcult.media.entities.Media;
import com.citadelcult.citadelcult.payment.PaymentsRepository;
import com.citadelcult.citadelcult.payment.entities.PaymentProvider;
import com.citadelcult.citadelcult.store.StoreRepository;
import com.citadelcult.citadelcult.store.entities.StoreSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    @Value("classpath:data/countries.json")
    private Resource countriesJson;

    @Value("classpath:data/currencies.json")
    private Resource currenciesJson;

    @Value("classpath:data/store-settings.json")
    private Resource storeSettingsJson;

    @Value("classpath:data/payment-providers.json")
    private Resource paymentProvidersJson;

    @Value("classpath:data/media.json")
    private Resource mediaJson;

    private final CurrenciesRepository currenciesRepository;
    private final CountriesRepository countriesRepository;
    private final StoreRepository storeRepository;
    private final PaymentsRepository paymentsRepository;
    private final MediaRepository mediaRepository;
    private final MarketsRepository marketsRepository;
    private final Environment env;

    @Override
    public void run(String... args) {
        boolean seedExecuted = false;

        if (currenciesRepository.count() == 0) {
            seedCurrencies();
            seedExecuted = true;
        }
        if (countriesRepository.count() == 0) {
            seedCountries();
            seedExecuted = true;
        }
        if (marketsRepository.count() == 0) {
            seedMarkets();
            seedExecuted = true;
        }
        if (storeRepository.count() == 0) {
            seedStoreSettings();
            seedExecuted = true;
        }
        if (paymentsRepository.count() == 0) {
            seedPaymentProviders();
            seedExecuted = true;
        }
        if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
            if (mediaRepository.count() == 0) {
                seedMedia();
                seedExecuted = true;
            }
        }
        if (seedExecuted) {
            log.info("🌱 Seeding of initial data completed.");
        }
    }

    private void seedCountries() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Country> countries = Arrays.asList(objectMapper.readValue(countriesJson.getInputStream(), Country[].class));

            countriesRepository.saveAll(countries);

            log.info("🌿 Initialization of countries was successful.");
        } catch (IOException e) {
            log.error("Error seeding countries: " + e.getMessage());
        }
    }

    private void seedCurrencies() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Currency> currencies = Arrays.asList(objectMapper.readValue(currenciesJson.getInputStream(), Currency[].class));

            currenciesRepository.saveAll(currencies);

            log.info("🌿 Initialization of currencies was successful.");
        } catch (IOException e) {
            log.error("Error seeding currencies: " + e.getMessage());
        }
    }

    private void seedStoreSettings() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<StoreSettings> storeSettings = Arrays.asList(objectMapper.readValue(storeSettingsJson.getInputStream(), StoreSettings[].class));

            storeRepository.saveAll(storeSettings);

            log.info("🌿 Initialization of store settings was successful.");
        } catch (IOException e) {
            log.error("Error seeding store settings: " + e.getMessage());
        }
    }

    private void seedMarkets() {
        Market marketNA = new Market();
        marketNA.setName("NA");
        var currencyUSD = currenciesRepository.findByCode("USD");
        marketNA.setCurrency(currencyUSD);
        var countryUS = countriesRepository.findByIso("US").orElseThrow(() -> new ResourceNotFoundException("Country not found"));
        marketNA.setCountries(Collections.singletonList(countryUS));
        marketNA = marketsRepository.save(marketNA);
        countryUS.setMarket(marketNA);
        countriesRepository.save(countryUS);

        Market marketUkraine = new Market();
        marketUkraine.setName("Ukraine");
        var currencyUAH = currenciesRepository.findByCode("UAH");
        marketUkraine.setCurrency(currencyUAH);
        var countryUkraine = countriesRepository.findByIso("UA").orElseThrow(() -> new ResourceNotFoundException("Country not found"));
        marketUkraine.setCountries(Collections.singletonList(countryUkraine));
        marketUkraine = marketsRepository.save(marketUkraine);
        countryUkraine.setMarket(marketUkraine);
        countriesRepository.save(countryUkraine);
    }

    private void seedMedia() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Media> mediaList = Arrays.asList(objectMapper.readValue(mediaJson.getInputStream(), Media[].class));

            mediaRepository.saveAll(mediaList);

            log.info("🌿 Initialization of media was successful.");
        } catch (IOException e) {
            log.error("Error seeding media: " + e.getMessage());
        }
    }

    private void seedPaymentProviders() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<PaymentProvider> paymentProviders = Arrays.asList(objectMapper.readValue(paymentProvidersJson.getInputStream(), PaymentProvider[].class));

            paymentsRepository.saveAll(paymentProviders);

            log.info("🌿 Initialization of payment providers was successful.");
        } catch (IOException e) {
            log.error("Error seeding payment providers: " + e.getMessage());
        }
    }
}
