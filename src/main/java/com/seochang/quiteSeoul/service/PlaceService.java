package com.seochang.quiteSeoul.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.seochang.quiteSeoul.domain.dto.FcstCongestDTO;
import com.seochang.quiteSeoul.domain.dto.FcstTodayDTO;
import com.seochang.quiteSeoul.domain.dto.PlaceCongestionDTO;
import com.seochang.quiteSeoul.domain.dto.PlaceEventDTO;
import com.seochang.quiteSeoul.domain.dto.PlaceEventListDTO;
import com.seochang.quiteSeoul.domain.dto.PlaceInfoDTO;
import com.seochang.quiteSeoul.domain.dto.PlaceWeatherDTO;
import com.seochang.quiteSeoul.repository.PlaceDataRepository;
import com.seochang.quiteSeoul.repository.PlaceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceDataRepository placeDataRepository;
    private static final int[] TARGET_WEATHER_INDICES = {0, 2, 5, 8};

    public List<String> findTop10Places() {
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

    public Optional<PlaceInfoDTO> getPlaceInfo(String placeName) {
        return placeRepository.findByPlaceName(placeName)
                .flatMap(place -> placeDataRepository.findplaceInfo(place.getPlaceId()))
                .flatMap(this::processPlaceInfo);
    }

    private Optional<PlaceInfoDTO> processPlaceInfo(String placeStatus) {

        Object placeWeather = JsonPath.read(placeStatus, "$.WEATHER_STTS");
        Object placeCongestion = JsonPath.read(placeStatus, "$.LIVE_PPLTN_STTS");
        Object placeEvent = JsonPath.read(placeStatus, "$.EVENT_STTS");

        PlaceInfoDTO placeInfoDTO = new PlaceInfoDTO();

        handleWeatherStatus(placeWeather.toString())
                .ifPresent(placeWeatherDTO -> {
                    placeInfoDTO.setPlaceWeatherDTO(placeWeatherDTO);
                });
        handleCongestionStatus(placeCongestion.toString())
                .ifPresent(placeCongestionDTO -> {
                    placeInfoDTO.setPlaceCongestionDTO(placeCongestionDTO);
                });
        handleEventStatus(placeEvent.toString())
                .ifPresent(placeEventListDTO -> {
                    placeInfoDTO.setPlaceEventListDTO(placeEventListDTO);
                });
        return Optional.of(placeInfoDTO);
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
                        fcstTodayDTO.setSkyStts(fcstNode.path("SKY_STTS").asText());
                        fcstTodayDTO.setPrecptType(fcstNode.path("PRECPT_TYPE").asText());
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

    private Optional<PlaceEventListDTO> handleEventStatus(String eventStatus) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(eventStatus);
            JsonNode eventsStatusNode = rootNode.get(0);

            PlaceEventListDTO placeEventListDTO = new PlaceEventListDTO();
            List<PlaceEventDTO> placeEventDTOList = new ArrayList<>();

            for (JsonNode eventNode : eventsStatusNode) {
                PlaceEventDTO placeEventDTO = new PlaceEventDTO();

                placeEventDTO.setUrl(eventNode.path("URL").asText());
                placeEventDTO.setIsFree(eventNode.path("PAY_YN").asText(null));
                placeEventDTO.setEventName(eventNode.path("EVENT_NM").asText());
                placeEventDTO.setThumbnail(eventNode.path("THUMBNAIL").asText());
                placeEventDTO.setEventPlace(eventNode.path("EVENT_PLACE").asText());
                placeEventDTO.setEventPeriod(eventNode.path("EVENT_PERIOD").asText());
                placeEventDTOList.add(placeEventDTO);
            }
            placeEventListDTO.setPlaceEventDTOList(placeEventDTOList);
            return Optional.of(placeEventListDTO);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


}
