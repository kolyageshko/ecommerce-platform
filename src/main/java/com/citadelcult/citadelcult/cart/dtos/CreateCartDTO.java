package com.citadelcult.citadelcult.cart.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateCartDTO {
    @JsonProperty("country_code")
    String countryCode;
}
