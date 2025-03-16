package com.citadelcult.citadelcult.country;

import com.citadelcult.citadelcult.country.entities.Country;
import com.citadelcult.citadelcult.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountriesService {

    private final CountriesRepository countriesRepository;

    public List<Country> getAllCountries() {
        return countriesRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Country createCountry(Country country) {
        return countriesRepository.save(country);
    }

    public Country findByIso(String iso) {
        return countriesRepository.findByIso(iso).orElse(null);
    }

    public Country getByIsoOrThrow(String countryCode) {
        Country country = findByIso(countryCode);
        if (country == null) {
            throw new ResourceNotFoundException("Country not found with code: " + countryCode);
        }
        return country;
    }
}
