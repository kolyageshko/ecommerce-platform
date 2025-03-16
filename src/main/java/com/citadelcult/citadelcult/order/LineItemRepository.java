package com.citadelcult.citadelcult.order;

import com.citadelcult.citadelcult.order.entities.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unused")
public interface LineItemRepository extends JpaRepository<LineItem, String> {
}
