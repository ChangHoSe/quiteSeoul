package com.seochang.quiteSeoul.data;

import com.seochang.quiteSeoul.domain.Place;
import com.seochang.quiteSeoul.domain.PlaceData;
import com.seochang.quiteSeoul.repository.PlaceDataRepository;
import com.seochang.quiteSeoul.repository.PlaceRepository;
import jakarta.annotation.PostConstruct;
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
public class PlaceDataService {

    private final PlaceRepository placeRepository;
    private final PlaceDataRepository placeDataRepository;

    public String[] tourismPlaces = {
            "강남 MICE 관광특구", "동대문 관광특구", "명동 관광특구", "이태원 관광특구", "잠실 관광특구", "종로·청계 관광특구",
            "홍대 관광특구", "경복궁", "광화문·덕수궁", "보신각", "서울 암사동 유적",
            "창덕궁·종묘", "가산디지털단지역", "강남역", "건대입구역", "고덕역",
            "고속터미널역", "교대역", "구로디지털단지역", "구로역", "군자역",
            "남구로역", "대림역", "동대문역", "뚝섬역", "미아사거리역",
            "발산역", "북한산우이역", "사당역", "삼각지역", "서울대입구역",
            "서울식물원·마곡나루역", "서울역", "선릉역", "성신여대입구역", "수유역",
            "신논현역·논현역", "신도림역", "신림역", "신촌·이대역", "양재역",
            "역삼역", "연신내역", "오목교역·목동운동장", "왕십리역", "용산역",
            "이태원역", "장지역", "장한평역", "천호역", "총신대입구(이수)역",
            "충정로역", "합정역", "혜화역", "홍대입구역(2호선)", "회기역",
            "4·19 카페거리", "가락시장", "가로수길", "광장(전통)시장", "김포공항",
            "낙산공원·이화마을", "노량진", "덕수궁길·정동길", "방배역 먹자골목", "북촌한옥마을",
            "서촌", "성수카페거리", "수유리 먹자골목", "쌍문동 맛집거리", "압구정로데오거리",
            "여의도", "연남동", "영등포 타임스퀘어", "외대앞", "용리단길",
            "이태원 앤틱가구거리", "인사동", "창동 신경제 중심지", "청담동 명품거리", "청량리 제기동 일대 전통시장",
            "해방촌·경리단길", "DDP(동대문디자인플라자)", "DMC(디지털미디어시티)", "강서한강공원", "고척돔",
            "광나루한강공원", "광화문광장", "국립중앙박물관·용산가족공원", "난지한강공원", "남산공원",
            "노들섬", "뚝섬한강공원", "망원한강공원", "반포한강공원", "북서울꿈의숲",
            "불광천", "서리풀공원·몽마르뜨공원", "서울광장", "서울대공원", "서울숲공원",
            "아차산", "양화한강공원", "어린이대공원", "여의도한강공원", "월드컵공원",
            "응봉산", "이촌한강공원", "잠실종합운동장", "잠실한강공원", "잠원한강공원",
            "청계산", "청와대", "북창동 먹자골목", "남대문시장", "익선동"
    };

//    @PostConstruct
//    public void init() {
//        processAllPlace(tourismPlaces);
//    }

    public void processAllPlace(String[] places) {
        for (String place : places) {
            searchAndSavePlaceData(place);
        }
    }

    @Value("${SEOUL_API_KEY}")
    private String apiKey;

    public void searchAndSavePlaceData(String placeName) {
        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088"); // URL
            urlBuilder.append("/" + URLEncoder.encode(apiKey, "UTF-8")); // 인증키
            urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8")); // 요청파일타입
            urlBuilder.append("/" + URLEncoder.encode("citydata", "UTF-8"));
            urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("/" + URLEncoder.encode("5", "UTF-8"));
            urlBuilder.append("/" + URLEncoder.encode(placeName, "UTF-8"));

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

            placeRepository.findByPlaceName(placeName)
                    .ifPresent(place -> {
                        placeData.setPlace(place);
                        placeDataRepository.save(placeData);
                    });

        } catch (IOException e) {
            log.error("API 요청 중 오류 발생", e);
        }
    }
}
