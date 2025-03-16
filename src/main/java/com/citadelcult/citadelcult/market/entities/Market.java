package com.citadelcult.citadelcult.market.entities;

import com.citadelcult.citadelcult.country.entities.Country;
import com.citadelcult.citadelcult.currency.entities.Currency;
import com.citadelcult.citadelcult.payment.entities.PaymentProvider;
import com.citadelcult.citadelcult.shipping.entities.ShippingMethod;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE market SET deleted_at = current_timestamp WHERE id=?")
@SQLRestriction("deleted_at is null")
public class Market {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Currency currency;

    @OneToMany(mappedBy = "market", cascade = CascadeType.MERGE)
    @JsonManagedReference
    @ToString.Exclude
    private List<Country> countries;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "market_payment_provider",
            joinColumns = @JoinColumn(name = "market_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_provider_id"))
    @JsonProperty("payment_providers")
    @ToString.Exclude
    private List<PaymentProvider> paymentProviders;

    @OneToMany(mappedBy = "market")
    @JsonProperty("shipping_methods")
    @JsonManagedReference
    @ToString.Exclude
    private List<ShippingMethod> shippingMethods;

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
        Market market = (Market) o;
        return getId() != null && Objects.equals(getId(), market.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
