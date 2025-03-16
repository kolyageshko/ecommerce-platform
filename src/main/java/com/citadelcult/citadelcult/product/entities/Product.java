package com.citadelcult.citadelcult.product.entities;

import com.citadelcult.citadelcult.media.entities.Media;
import com.citadelcult.citadelcult.product.enums.ProductStatus;
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
@Table(name = "product")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE product SET deleted_at = current_timestamp WHERE id=?")
@SQLRestriction("deleted_at is null")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "handle", nullable = false, unique = true)
    private String handle;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference(value="variants")
    @ToString.Exclude
    private List<ProductVariant> variants;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "product_media",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id"))
    @ToString.Exclude
    private List<Media> media;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "thumbnail_id", referencedColumnName = "id")
    private Media thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @CreationTimestamp
    @JsonProperty("created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    @JsonProperty("deleted_at")
    private Instant deletedAt;

    @SuppressWarnings("unused")
    @JsonProperty("available")
    public boolean isAvailable() {
        if (variants == null || variants.isEmpty()) {
            return false;
        }
        for (ProductVariant variant : variants) {
            if (variant.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @PrePersist
    private void ensureThumbnailAndHandle() {
        ensureThumbnail();
        ensureStatus();
    }

    private void ensureThumbnail() {
        if (this.thumbnail == null && this.media != null) {
            this.thumbnail = this.media.get(0);
        }
    }

    private void ensureStatus() {
        if (this.status == null) {
            this.status = ProductStatus.DRAFT;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Product product = (Product) o;
        return getId() != null && Objects.equals(getId(), product.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
