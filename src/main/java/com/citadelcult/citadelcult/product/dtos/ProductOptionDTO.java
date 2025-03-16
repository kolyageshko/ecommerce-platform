package com.citadelcult.citadelcult.product.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ProductOptionDTO {
    String name;
    List<String> values;
}
