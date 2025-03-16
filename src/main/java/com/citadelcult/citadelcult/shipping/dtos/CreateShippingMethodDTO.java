package com.citadelcult.citadelcult.shipping.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.citadelcult.citadelcult.shipping.entities.ShippingMethod}
 */
@Value
public class CreateShippingMethodDTO implements Serializable {
    @NotNull
    @NotEmpty
    String name;

    String description;

    @NotNull
    @PositiveOrZero
    BigDecimal price;

    @JsonProperty("market_id")
    @NotNull
    Long marketId;
}