package com.citadelcult.citadelcult.country;

import com.citadelcult.citadelcult.country.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountriesRepository extends JpaRepository<Country, Long> {
    List<Country> findByIsoIn(List<String> isoCodes);
    Optional<Country> findByIso(String iso);
}
