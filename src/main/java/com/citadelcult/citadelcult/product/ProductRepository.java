package com.citadelcult.citadelcult.product;

import com.citadelcult.citadelcult.product.entities.Product;
import com.citadelcult.citadelcult.product.enums.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByHandle(String handle);
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
