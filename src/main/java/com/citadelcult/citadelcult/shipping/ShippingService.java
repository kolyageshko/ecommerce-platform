package com.citadelcult.citadelcult.shipping;

import com.citadelcult.citadelcult.market.MarketsService;
import com.citadelcult.citadelcult.shipping.dtos.CreateShippingMethodDTO;
import com.citadelcult.citadelcult.shipping.entities.ShippingMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShippingMethodRepository shippingMethodRepository;
    private final MarketsService marketService;

    @PreAuthorize("hasRole('ADMIN')")
    public ShippingMethod createShippingMethod(CreateShippingMethodDTO shippingMethodDTO) {
        var market = marketService.getMarketByIdOrThrow(shippingMethodDTO.getMarketId());

        var shippingMethod = new ShippingMethod();
        shippingMethod.setName(shippingMethodDTO.getName());
        shippingMethod.setDescription(shippingMethodDTO.getDescription());
        shippingMethod.setPrice(shippingMethodDTO.getPrice());

        shippingMethod.setMarket(market);

        return shippingMethodRepository.save(shippingMethod);
    }

    public ShippingMethod findById(String shippingMethodId) {
        return shippingMethodRepository.findById(shippingMethodId).orElse(null);
    }
}
