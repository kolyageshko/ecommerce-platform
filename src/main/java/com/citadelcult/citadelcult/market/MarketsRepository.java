package com.citadelcult.citadelcult.market;

import com.citadelcult.citadelcult.market.entities.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketsRepository extends JpaRepository<Market, Long> {
    Optional<Market> findByCountriesIso(String countryCode);
}
