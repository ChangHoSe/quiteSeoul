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
}
