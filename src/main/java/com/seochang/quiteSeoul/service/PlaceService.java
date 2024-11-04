package com.seochang.quiteSeoul.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seochang.quiteSeoul.domain.Place;
import com.seochang.quiteSeoul.domain.PlaceData;
import com.seochang.quiteSeoul.domain.dto.FcstCongestDTO;
import com.seochang.quiteSeoul.domain.dto.FcstTodayDTO;
import com.seochang.quiteSeoul.domain.dto.PlaceCongestionDTO;
import com.seochang.quiteSeoul.domain.dto.PlaceWeatherDTO;
import com.seochang.quiteSeoul.repository.PlaceDataRepository;
import com.seochang.quiteSeoul.repository.PlaceRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceDataRepository placeDataRepository;
    private static final int[] TARGET_WEATHER_INDICES = {0, 2, 5, 8};

    @Value("${SEOUL_API_KEY}")
    private String apiKey;

    public List<String> initialPlace() {
        //TODO : 공간 유형 매개변수 처리
        List<String> placeIds = placeDataRepository.findTop10PlacesIds();
        List<String> placeNames = new ArrayList<>();
        for (String placeId : placeIds) {
            Integer id = Integer.parseInt(placeId);
            String placeName = placeRepository.findById(id).get().getPlaceName();
            placeNames.add(placeName);
        }
        return placeNames;
    }

    public Optional<PlaceWeatherDTO> getWeatherInfoByRegion(String placeName) {
        return placeRepository.findByPlaceName(placeName)
                .flatMap(place -> placeDataRepository.findLastestWeatherStatus(place.getPlaceId()))
                .flatMap(this::handleWeatherStatus);
    }

    public Optional<PlaceCongestionDTO> getCongestionInfoByRegion(String placeName) {
        return placeRepository.findByPlaceName(placeName)
                .flatMap(place -> placeDataRepository.findLastestCongestionStatus(place.getPlaceId()))
                .flatMap(this::handleCongestionStatus);
    }

    private Optional<PlaceWeatherDTO> handleWeatherStatus(String weatherStatus) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(weatherStatus);
            JsonNode weatherStatusNode = rootNode.get(0).get(0);

            PlaceWeatherDTO placeWeatherDTO = new PlaceWeatherDTO();
            placeWeatherDTO.setPm10(Integer.parseInt(weatherStatusNode.path("PM10").asText()));
            placeWeatherDTO.setPm25(Integer.parseInt(weatherStatusNode.path("PM25").asText()));
            placeWeatherDTO.setPm10Index(weatherStatusNode.path("PM10_INDEX").asText());
            placeWeatherDTO.setPm25Index(weatherStatusNode.path("PM25_INDEX").asText());

            placeWeatherDTO.setTemperature(Double.parseDouble(weatherStatusNode.path("TEMP").asText()));
            placeWeatherDTO.setMaxTemp(Double.parseDouble(weatherStatusNode.path("MAX_TEMP").asText()));
            placeWeatherDTO.setMinTemp(Double.parseDouble(weatherStatusNode.path("MIN_TEMP").asText()));
            placeWeatherDTO.setAirIdx(weatherStatusNode.path("AIR_IDX").asText());
            placeWeatherDTO.setHumidity(Integer.parseInt(weatherStatusNode.path("HUMIDITY").asText()));
            placeWeatherDTO.setSensibleTemp(Double.parseDouble(weatherStatusNode.path("SENSIBLE_TEMP").asText()));

            // 24시간 예보 목록 추출 및 설정
            List<FcstTodayDTO> fcstTodayDTOList = new ArrayList<>();
            JsonNode fcst24HoursArray = weatherStatusNode.path("FCST24HOURS");

            for (int i = 0; i < fcst24HoursArray.size(); i++) {
                JsonNode fcstNode = fcst24HoursArray.get(i);
                for (int targetIndex: TARGET_WEATHER_INDICES) {
                    if (i == targetIndex) {
                        FcstTodayDTO fcstTodayDTO = new FcstTodayDTO();
                        fcstTodayDTO.setTemp(Integer.parseInt(fcstNode.path("TEMP").asText()));
                        fcstTodayDTO.setFcstDt(fcstNode.path("FCST_DT").asText());
                        fcstTodayDTO.setSkyStts(fcstNode.path("PRECPT_TYPE").asText());
                        fcstTodayDTO.setRainChance(Integer.parseInt(fcstNode.path("RAIN_CHANCE").asText()));
                        fcstTodayDTO.setPrecipitation(fcstNode.path("PRECIPITATION").asText());

                        fcstTodayDTOList.add(fcstTodayDTO);
                        break;
                    }
                }
            }

            placeWeatherDTO.setFcstTodayDTOList(fcstTodayDTOList);
            return Optional.of(placeWeatherDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<PlaceCongestionDTO> handleCongestionStatus(String congestionStatus) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(congestionStatus);
            JsonNode congestionStatusNode = rootNode.get(0).get(0);

            PlaceCongestionDTO placeCongestionDTO = new PlaceCongestionDTO();
            placeCongestionDTO.setAreaCongestLvl(congestionStatusNode.path("AREA_CONGEST_LVL").asText());
            placeCongestionDTO.setAreaCongestMsg(congestionStatusNode.path("AREA_CONGEST_MSG").asText());

            List<FcstCongestDTO> fcstCongestDTOList = new ArrayList<>();
            JsonNode fcst24HoursArray = congestionStatusNode.path("FCST_PPLTN");

            for (int i = 0; i < fcst24HoursArray.size(); i++) {
                JsonNode fcstNode = fcst24HoursArray.get(i);
                FcstCongestDTO fcstCongestDTO = new FcstCongestDTO();
                fcstCongestDTO.setFcstCongestLvl(fcstNode.path("FCST_CONGEST_LVL").asText());
                fcstCongestDTO.setFcstTime(fcstNode.path("FCST_TIME").asText());
                fcstCongestDTO.setFcstMax(Integer.parseInt(fcstNode.path("FCST_PPLTN_MAX").asText()));
                fcstCongestDTO.setFcstMin(Integer.parseInt(fcstNode.path("FCST_PPLTN_MIN").asText()));
                fcstCongestDTOList.add(fcstCongestDTO);
            }

            placeCongestionDTO.setFcstCongestDTO(fcstCongestDTOList);
            return Optional.of(placeCongestionDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
