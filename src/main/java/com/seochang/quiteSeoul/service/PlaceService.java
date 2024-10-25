package com.seochang.quiteSeoul.service;

import com.seochang.quiteSeoul.domain.Place;
import com.seochang.quiteSeoul.domain.PlaceData;
import com.seochang.quiteSeoul.repository.PlaceDataRepository;
import com.seochang.quiteSeoul.repository.PlaceRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
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




    @Value("${SEOUL_API_KEY}")
    private String apiKey;

    public void searchAndSavePlaceData(String placeName) {
        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088"); // URL
            urlBuilder.append("/" + URLEncoder.encode(apiKey, "UTF-8")); // 인증키
            urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8")); // 요청파일타입 (xml, json 등)
            urlBuilder.append("/" + URLEncoder.encode("citydata", "UTF-8")); // 서비스명
            urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8")); // 요청 시작위치
            urlBuilder.append("/" + URLEncoder.encode("5", "UTF-8")); // 요청 종료위치
            urlBuilder.append("/" + URLEncoder.encode(placeName, "UTF-8")); // 추가 요청 인자 (예: 날짜)

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json"); // 요청 타입을 json으로 설정

            int responseCode = conn.getResponseCode();
            log.info("Response code: " + responseCode);

            BufferedReader rd;
            if (responseCode >= 200 && responseCode <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONObject filteredData = new JSONObject();

            filteredData.append("LIVE_PPLTN_STTS", jsonObject.getJSONObject("CITYDATA").getJSONArray("LIVE_PPLTN_STTS"));
            filteredData.append("WEATHER_STTS", jsonObject.getJSONObject("CITYDATA").getJSONArray("WEATHER_STTS"));
            filteredData.append("EVENT_STTS", jsonObject.getJSONObject("CITYDATA").getJSONArray("EVENT_STTS"));

            log.info("API Response filtered: " + filteredData.toString());

            PlaceData placeData = new PlaceData();
            placeData.setPlaceInformation(filteredData.toString());
            placeData.setCollectedAt(LocalDateTime.now());

            Optional<Place> place = placeRepository.findById(1);

            placeData.setPlace(place.get());

            placeDataRepository.save(placeData);

        } catch (IOException e) {
            log.error("API 요청 중 오류 발생", e);
        }
    }
}
