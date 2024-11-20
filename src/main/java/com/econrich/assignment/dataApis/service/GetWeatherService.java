package com.econrich.assignment.dataApis.service;

import com.econrich.assignment.common.exception.CustomUniversalException;
import com.econrich.assignment.dataApis.dto.KoreaLocationsDto;
import com.econrich.assignment.dataApis.dto.WeatherDto;
import com.econrich.assignment.dataApis.entity.KoreaLocations;
import com.econrich.assignment.dataApis.repo.KoreaLocationsCustomRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetWeatherService {

    private final KoreaLocationsCustomRepository koreaLocationsCustomRepository;

    @Value("${data.apis.encodingKey}")
    private String encodingKey;


    @Transactional
    public WeatherDto.LocationsSummary execute(String si, String gu, String dong) {

        KoreaLocationsDto.LocationsCondition condition = new KoreaLocationsDto.LocationsCondition();
        condition.setSi(si);
        if (StringUtils.isNotBlank(gu)) condition.setGu(gu);
        if (StringUtils.isNotBlank(dong)) condition.setDong(dong);
        KoreaLocations koreaLocations = koreaLocationsCustomRepository.findAllByLocation(condition).stream().findFirst()
                .orElseThrow(() -> new CustomUniversalException(HttpStatus.BAD_REQUEST, "등록되지 않은 지역입니다."));


        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0");
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
                .uriBuilderFactory(factory)
                .build();

        WeatherDto.ReturnWeatherApi.Response.Body.Items items = Objects.requireNonNull(webClient.get()
                        .uri(uriBuilder ->
                                uriBuilder.path("/getUltraSrtNcst")
                                        .queryParam("serviceKey", encodingKey)
                                        .queryParam("numOfRows", 10)
                                        .queryParam("pageNo", 1)
                                        .queryParam("dataType", "JSON")
                                        .queryParam("base_date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                                        .queryParam("base_time", LocalDateTime.now().getHour() + "00")
                                        .queryParam("nx", koreaLocations.getGridX())
                                        .queryParam("ny", koreaLocations.getGridY())
                                        .build())
                        .retrieve()
                        .bodyToMono(WeatherDto.ReturnWeatherApi.class)
                        .doOnError(e -> log.error(e.getMessage()))
                        .block())
                .getResponse()
                .getBody()
                .getItems();

        return WeatherDto.LocationsSummary.builder()
                .si(koreaLocations.getSi())
                .gu(koreaLocations.getGu())
                .dong(koreaLocations.getDong())
                .weatherSummary(items.getItem().stream().map(item -> {
                            WeatherDto.Category category = WeatherDto.Category.valueOf(item.getCategory());
                            if (item.getCategory().equals("PTY")) {
                                String value = switch (item.getObsrValue()) {
                                    case "1" -> "비";
                                    case "2" -> "비/눈";
                                    case "3" -> "눈";
                                    case "5" -> "빗방울";
                                    case "6" -> "빗발울눈날림";
                                    case "7" -> "눈날림";
                                    default -> "없음";
                                };
                                return WeatherDto.WeatherSummary.builder()
                                        .category(category.getText())
                                        .obsrValue(value)
                                        .build();
                            }
                            return WeatherDto.WeatherSummary.builder()
                                    .category(category.getText())
                                    .obsrValue(item.getObsrValue() + category.getValue())
                                    .build();
                        }
                ).collect(Collectors.toList()))
                .build();


    }
}
