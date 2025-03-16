package com.citadelcult.citadelcult.cart.entities;

import com.citadelcult.citadelcult.product.entities.MoneyAmount;
import com.citadelcult.citadelcult.product.entities.Product;
import com.citadelcult.citadelcult.product.entities.ProductVariant;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "cart_line_item")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE cart_line_item SET deleted_at = current_timestamp WHERE id=?")
@SQLRestriction("deleted_at is null")
public class CartLineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant variant;

    @CreationTimestamp
    @JsonProperty("created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    @JsonProperty("deleted_at")
    private Instant deletedAt;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    @ToString.Exclude
    private Cart cart;

    @JsonProperty("product")
    public Product getProduct() {
        if (variant != null && variant.getProduct() != null) {
            return variant.getProduct();
        }
        return null;
    }

    public BigDecimal getEffectivePrice(String currencyCode) {
        if (variant == null || currencyCode == null || currencyCode.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<MoneyAmount> prices = variant.getPrices();
        if (prices == null || prices.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Iterate through prices to find the one matching the given currencyCode
        for (MoneyAmount price : prices) {
            if (currencyCode.equals(price.getCurrency().getCode())) {
                if (price.getSalePrice() != null) {
                    return price.getSalePrice();
                }
                return price.getPrice();
            }
        }

        // If no matching currency is found, you can decide on a fallback price
        // For instance, return zero or the price in a default currency
        return BigDecimal.ZERO; // Fallback behavior
    }

}
