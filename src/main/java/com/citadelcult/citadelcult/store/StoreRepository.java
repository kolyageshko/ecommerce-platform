package com.citadelcult.citadelcult.store;

import com.citadelcult.citadelcult.store.entities.StoreSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<StoreSettings, String> {
    StoreSettings findTopById(String id);

}
