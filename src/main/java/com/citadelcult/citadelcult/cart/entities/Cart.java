package com.citadelcult.citadelcult.cart.entities;

import com.citadelcult.citadelcult.market.entities.Market;
import com.citadelcult.citadelcult.order.entities.Address;
import com.citadelcult.citadelcult.payment.entities.PaymentProvider;
import com.citadelcult.citadelcult.shipping.entities.ShippingMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.*;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE cart SET deleted_at = current_timestamp WHERE id=?")
@SQLRestriction("deleted_at is null")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "token")
    private String token;

    @ManyToOne
    @JoinColumn(name = "market_id")
    private Market market;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.MERGE, orphanRemoval = true)
    @JsonProperty("line_items")
    private List<CartLineItem> lineItems;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    @JsonProperty("shipping_address")
    private Address shippingAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id")
    @JsonProperty("billing_address")
    private Address billingAddress;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id")
    @JsonProperty("shipping_method")
    private ShippingMethod shippingMethod;

    @ManyToOne
    @JoinColumn(name = "payment_provider_id")
    @JsonProperty("payment_provider")
    private PaymentProvider paymentProvider;

    @JdbcTypeCode(SqlTypes.JSON)
    private Context context;

    @CreationTimestamp
    @JsonProperty("created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    @JsonProperty("deleted_at")
    private Instant deletedAt;

    @JsonProperty("completed_at")
    private Instant completedAt;

    @JsonProperty("shipping_price")
    public BigDecimal getShippingPrice() {
        if (this.shippingMethod == null) {
            return BigDecimal.ZERO;
        }
        return this.shippingMethod.getPrice();
    }

    @JsonProperty("subtotal")
    public BigDecimal getSubtotal() {
        if (this.lineItems == null) {
            return BigDecimal.ZERO;
        }

        var currency = market.getCurrency();
        return this.lineItems.stream()
                .map(lineItem -> {
                    BigDecimal price = lineItem.getEffectivePrice(currency.getCode());
                    return price.multiply(BigDecimal.valueOf(lineItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @JsonProperty("total")
    public BigDecimal getTotal() {
        return getSubtotal().add(getShippingPrice());
    }

    @JsonProperty("item_count")
    public int getItemCount() {
        if (this.lineItems == null) {
            return 0;
        }
        return this.lineItems.stream()
                .mapToInt(CartLineItem::getQuantity)
                .sum();
    }

    @PrePersist
    protected void persistToken() {
        if (this.token == null || this.token.isEmpty()) {
            this.token = UUID.randomUUID().toString();
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Cart cart = (Cart) o;
        return getId() != null && Objects.equals(getId(), cart.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
