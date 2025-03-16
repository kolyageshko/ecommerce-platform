package com.citadelcult.citadelcult.country.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CountryListRequest {
    private List<String> countries;
}
