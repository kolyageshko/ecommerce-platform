package com.citadelcult.citadelcult.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.citadelcult.citadelcult.product.entities.MoneyAmount}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyAmountDTO implements Serializable {
    @JsonProperty("currency_code")
    @NonNull
    String currencyCode;
    BigDecimal price;
    BigDecimal salePrice;
}
