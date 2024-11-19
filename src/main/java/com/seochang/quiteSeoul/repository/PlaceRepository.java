package com.seochang.quiteSeoul.repository;

import com.seochang.quiteSeoul.domain.Place;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {
    Optional<Place> findByPlaceName(String placeName);
}
