package com.seochang.quiteSeoul.repository;

import com.seochang.quiteSeoul.domain.Place;
import com.seochang.quiteSeoul.domain.PlaceData;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceDataRepository extends JpaRepository<PlaceData, Integer> {

}
