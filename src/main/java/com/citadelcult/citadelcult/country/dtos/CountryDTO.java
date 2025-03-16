package com.citadelcult.citadelcult.country.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CountryDTO {
    @JsonProperty("country_code")
    private String countryCode;
}
