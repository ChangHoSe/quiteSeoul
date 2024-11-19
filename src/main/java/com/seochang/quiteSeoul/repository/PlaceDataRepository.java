package com.seochang.quiteSeoul.repository;

import com.seochang.quiteSeoul.domain.Place;
import com.seochang.quiteSeoul.domain.PlaceData;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlaceDataRepository extends JpaRepository<PlaceData, Integer> {

    @Query(value = """
SELECT pd.place_id
FROM placedata pd
JOIN (
    SELECT place_id, MAX(collected_at) AS latest_collected_at
    FROM placedata
    WHERE JSON_EXTRACT(place_information, '$.LIVE_PPLTN_STTS[0][0].AREA_CONGEST_LVL') IN ('보통', '여유')
    GROUP BY place_id
) AS latest_data
ON pd.place_id = latest_data.place_id
AND pd.collected_at = latest_data.latest_collected_at
WHERE JSON_EXTRACT(pd.place_information, '$.LIVE_PPLTN_STTS[0][0].AREA_CONGEST_LVL') IN ('보통', '여유')
ORDER BY JSON_EXTRACT(pd.place_information, '$.LIVE_PPLTN_STTS[0][0].AREA_PPLTN_MIN') ASC
LIMIT 10;
""", nativeQuery = true)
    List<String> findTop10PlacesIds(); // TODO : 선호 테마 파라미터로 넣어서 관련 장소만 출력 예정

    @Query(value = """
SELECT place_information
FROM placedata
WHERE place_id = :placeId
ORDER BY collected_at DESC
LIMIT 1
""",nativeQuery = true)
    Optional<String> findplaceInfo(Integer placeId);

}
