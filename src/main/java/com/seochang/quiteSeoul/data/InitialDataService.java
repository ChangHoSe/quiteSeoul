package com.seochang.quiteSeoul.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.seochang.quiteSeoul.domain.Place;
import com.seochang.quiteSeoul.domain.PlaceType;
import com.seochang.quiteSeoul.repository.PlaceRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitialDataService {

    private final PlaceRepository placeRepository;

//    @PostConstruct
//    public void init() {
//        initialPlaceData();
//    }

    public void initialPlaceData() {
        log.info("프로그램 시작");

        // ClassPathResource를 사용하여 resources 폴더의 파일에 접근
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("initialPlaceData.csv"));
             CSVReader csvReader = new CSVReader(reader)) {

            csvReader.readNext(); // 헤더 스킵

            List<String[]> allRows = csvReader.readAll();
            for (String[] row : allRows) {
                String placeType = row[0];
                String placeName = row[1];
                String desc = row[2];

                Place place = new Place();
                if (placeType.equals("관광특구")) {
                    place.setPlaceType(PlaceType.TOURIST_SPECIAL_ZONE);
                } else if (placeType.equals("고궁·문화유산")) {
                    place.setPlaceType(PlaceType.CULTURAL_HERITAGE);
                } else if (placeType.equals("인구밀집지역")) {
                    place.setPlaceType(PlaceType.POPULATION_DENSE_AREA);
                } else if (placeType.equals("발달상권")) {
                    place.setPlaceType(PlaceType.DEVELOPED_COMMERCIAL_AREA);
                } else if (placeType.equals("공원")) {
                    place.setPlaceType(PlaceType.PARK);
                }

                place.setPlaceName(placeName);
                place.setDescription(desc);
                placeRepository.save(place);
            }
            log.info("초기 데이터 입력 완료");

        } catch (IOException | CsvException e) {
            log.error("CSV 파일 읽기 오류", e);
        }
    }
}
