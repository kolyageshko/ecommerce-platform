package com.citadelcult.citadelcult.cart.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AddLineItemDTO {
    @JsonProperty("variant_id")
    private Long variantId;

    @Min(value = 1, message = "Quantity should be at least 1")
    private int quantity;
}
