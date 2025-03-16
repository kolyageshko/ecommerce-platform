package com.citadelcult.citadelcult.store.entities;

import com.citadelcult.citadelcult.currency.entities.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "store")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class StoreSettings {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "is_store_open")
    @JsonProperty("is_store_open")
    private Boolean isStoreOpen = false;

    @OneToOne
    @JoinColumn(name = "currency_id")
    @JsonProperty("default_currency")
    private Currency defaultCurrency;

    @OneToMany
    @JoinTable(
            name = "store_currencies",
            joinColumns = @JoinColumn(name = "store_settings_id"),
            inverseJoinColumns = @JoinColumn(name = "currency_id")
    )
    @ToString.Exclude
    private List<Currency> currencies;

    @PrePersist
    private void ensureEntity() {
        this.setId(id == null ? "store_" + UUID.randomUUID() : id);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        StoreSettings that = (StoreSettings) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
