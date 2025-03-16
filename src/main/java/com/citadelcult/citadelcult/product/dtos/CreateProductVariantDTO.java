package com.citadelcult.citadelcult.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.citadelcult.citadelcult.product.entities.ProductVariant}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductVariantDTO implements Serializable {
    @NotNull
    @NotEmpty
    @NotBlank
    String name;

    String sku;

    @JsonProperty("inventory_stock")
    Integer inventoryStock;

    @JsonProperty("manage_inventory")
    Boolean manageInventory;

    @JsonProperty("allow_backorder")
    Boolean allowBackorder;

    List<MoneyAmountDTO> prices;
}