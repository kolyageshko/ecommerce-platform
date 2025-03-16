package com.citadelcult.citadelcult.product.entities;

import com.citadelcult.citadelcult.cart.entities.CartLineItem;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "product_variant")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@SQLDelete(sql = "UPDATE product_variant SET deleted_at = current_timestamp WHERE id=?")
@SQLRestriction("deleted_at is null")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "sku")
    private String sku;

    @Column(name = "inventory_stock")
    @JsonProperty("inventory_stock")
    private Integer inventoryStock = 0;

    @Column(name = "manage_inventory")
    @JsonProperty("manage_inventory")
    private Boolean manageInventory = false;

    @Column(name = "allow_backorder")
    @JsonProperty("allow_backorder")
    private Boolean allowBackorder = false;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference(value="prices")
    @ToString.Exclude
    private List<MoneyAmount> prices;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @ToString.Exclude
    private List<CartLineItem> cartLineItems;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    @JsonBackReference(value="variants")
    @ToString.Exclude
    private Product product;

    @CreationTimestamp
    @JsonProperty("created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    @JsonProperty("deleted_at")
    private Instant deletedAt;

    @JsonProperty("available")
    public boolean isAvailable() {
        boolean allowBackorder = getAllowBackorder() != null ? getAllowBackorder() : false;
        boolean manageInventory = getManageInventory() != null ? getManageInventory() : false;
        int inventoryStock = getInventoryStock() != null ? getInventoryStock() : 0;

        return (!manageInventory || inventoryStock > 0) || allowBackorder;
    }

    public boolean hasPriceInCurrency(String currencyCode) {
        if (prices != null) {
            for (MoneyAmount price : prices) {
                if (price.getCurrency().getCode().equals(currencyCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    public MoneyAmount getPriceByCurrencyCode(String currencyCode) {
        if (prices != null) {
            for (MoneyAmount price : prices) {
                if (price.getCurrency().getCode().equals(currencyCode)) {
                    return price;
                }
            }
        }
        return null;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ProductVariant that = (ProductVariant) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
