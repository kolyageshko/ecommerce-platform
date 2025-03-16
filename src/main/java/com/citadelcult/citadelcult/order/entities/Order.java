package com.citadelcult.citadelcult.order.entities;

import com.citadelcult.citadelcult.cart.entities.Cart;
import com.citadelcult.citadelcult.currency.entities.Currency;
import com.citadelcult.citadelcult.market.entities.Market;
import com.citadelcult.citadelcult.payment.entities.PaymentProvider;
import com.citadelcult.citadelcult.shipping.entities.ShippingMethod;
import com.citadelcult.citadelcult.user.User;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE orders SET deleted_at = current_timestamp WHERE id=?")
@SQLRestriction("deleted_at is null")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "external_id")
    @JsonProperty("external_id")
    private String externalId;

    @Column(name = "token")
    private String token;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @JsonProperty("payment_status")
    private OrderPaymentStatus paymentStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id")
    @JsonProperty("shipping_address")
    private Address shippingAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id")
    @JsonProperty("billing_address")
    private Address billingAddress;

    @ManyToOne
    @JoinColumn(name = "payment_provider_id")
    @JsonProperty("payment_provider")
    private PaymentProvider paymentProvider;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id")
    @JsonProperty("shipping_method")
    private ShippingMethod shippingMethod;

    @Column(name = "shipping_price")
    @JsonProperty("shipping_price")
    private BigDecimal shippingPrice;

    @ManyToOne
    @JoinColumn(name = "market_id", nullable = false)
    private Market market;

    @ManyToOne
    @JoinColumn(name = "currency_code")
    private Currency currency;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonProperty("line_items")
    @ToString.Exclude
    private List<LineItem> lineItems;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @JsonProperty("created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    @JsonProperty("deleted_at")
    private Instant deletedAt;

    @Transient
    @JsonProperty("payment_url")
    private String paymentUrl;

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
        return this.lineItems.stream()
                .map(item -> item.getEffectivePrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @JsonProperty("total")
    public BigDecimal getTotal() {
        return getSubtotal().add(getShippingPrice());
    }

    public int getItemsCount() {
        int totalItemCount = 0;
        if (lineItems == null) {
            return totalItemCount;
        }

        for (LineItem lineItem : lineItems) {
            totalItemCount += lineItem.getQuantity();
        }
        return totalItemCount;
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
        Order order = (Order) o;
        return getId() != null && Objects.equals(getId(), order.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
