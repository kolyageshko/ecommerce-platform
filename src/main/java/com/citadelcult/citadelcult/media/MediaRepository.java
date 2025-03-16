package com.citadelcult.citadelcult.media;

import com.citadelcult.citadelcult.media.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Media, String> {
    Media findByUrl(String url);
}
