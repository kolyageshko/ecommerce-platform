package com.citadelcult.citadelcult.market.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateMarketDTO {
    private String name;
    @JsonProperty("currency_code")
    private String currencyCode;
    private List<String> countries;
    @JsonProperty("payment_providers")
    private List<String> paymentProviders;
}
