package com.citadelcult.citadelcult.product;

import com.citadelcult.citadelcult.product.entities.MoneyAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyAmountRepository extends JpaRepository<MoneyAmount, String> {
}
