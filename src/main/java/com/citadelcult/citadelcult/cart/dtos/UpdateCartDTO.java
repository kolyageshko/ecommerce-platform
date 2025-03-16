package com.citadelcult.citadelcult.cart.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UpdateCartDTO {
    String email;

    @JsonProperty("shipping_address")
    AddressDTO shippingAddressDTO;

    @JsonProperty("billing_address")
    AddressDTO billingAddressDTO;

    @JsonProperty("shipping_method_id")
    String shippingMethodId;

    @JsonProperty("payment_provider_id")
    String paymentProviderId;
}
