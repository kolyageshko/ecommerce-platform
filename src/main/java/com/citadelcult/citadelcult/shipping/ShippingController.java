package com.citadelcult.citadelcult.shipping;

import com.citadelcult.citadelcult.shipping.dtos.CreateShippingMethodDTO;
import com.citadelcult.citadelcult.shipping.entities.ShippingMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping
    public ShippingMethod createShippingMethod(@Validated @RequestBody CreateShippingMethodDTO createShippingMethodDTO) {
        return shippingService.createShippingMethod(createShippingMethodDTO);
    }
}
