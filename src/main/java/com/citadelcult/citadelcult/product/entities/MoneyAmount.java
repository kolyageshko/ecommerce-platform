package com.citadelcult.citadelcult.product.entities;

import com.citadelcult.citadelcult.currency.entities.Currency;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "money_amount")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@SQLDelete(sql = "UPDATE money_amount SET deleted_at = current_timestamp WHERE id=?")
@SQLRestriction("deleted_at is null")
public class MoneyAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "sale_price")
    @JsonProperty("sale_price")
    private BigDecimal salePrice;

    @ManyToOne
    @JoinColumn(name = "currency_code", nullable = false)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    @JsonBackReference(value="prices")
    @ToString.Exclude
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MoneyAmount that = (MoneyAmount) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
