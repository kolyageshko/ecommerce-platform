package com.citadelcult.citadelcult.product.dtos;

import com.citadelcult.citadelcult.product.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDTO {
    private String name;
    private String handle;
    private ProductStatus status;
    private String thumbnail;
    private List<String> media;
    private List<CreateProductVariantDTO> variants;
}