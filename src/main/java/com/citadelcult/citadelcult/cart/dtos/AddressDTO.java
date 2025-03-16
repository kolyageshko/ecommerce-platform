package com.citadelcult.citadelcult.cart.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Value;

@Value
public class AddressDTO {
    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    @JsonProperty("address_1")
    String address1;

    @JsonProperty("address_2")
    String address2;

    @JsonProperty("country_code")
    String countryCode;

    @JsonProperty("postal_code")
    String postalCode;

    String province;

    String city;

    String phone;
}
