package com.citadelcult.citadelcult.product;

import com.citadelcult.citadelcult.product.entities.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    @Modifying
    @Query("UPDATE ProductVariant pv SET pv.inventoryStock = pv.inventoryStock - ?2 WHERE pv.id = ?1")
    void updateInventoryStock(Long variantId, int quantity);
}
