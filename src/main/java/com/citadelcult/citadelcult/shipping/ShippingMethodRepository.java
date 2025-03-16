package com.citadelcult.citadelcult.shipping;

import com.citadelcult.citadelcult.shipping.entities.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, String> {
}