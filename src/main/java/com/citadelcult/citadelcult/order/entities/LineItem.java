package com.citadelcult.citadelcult.order.entities;

import com.citadelcult.citadelcult.media.entities.Media;
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
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "line_item")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE line_item SET deleted_at = current_timestamp WHERE id=?")
@SQLRestriction("deleted_at is null")
public class LineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("sale_price")
    private BigDecimal salePrice;

    @ManyToOne
    @JoinColumn(name = "thumbnail_id")
    private Media thumbnail;

    private int quantity;

    @Column(name = "variant_name")
    @JsonProperty("variant_name")
    String variantName;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant variant;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @CreationTimestamp
    @JsonProperty("created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    @JsonProperty("deleted_at")
    private Instant deletedAt;

    public BigDecimal getEffectivePrice() {
        if (salePrice != null) {
            return salePrice;
        }
        return price;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        LineItem lineItem = (LineItem) o;
        return getId() != null && Objects.equals(getId(), lineItem.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
