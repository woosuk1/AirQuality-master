package com.example.zzyzzy.airquality.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.URLDecoder;

@Slf4j
@Service
public class AirQualityService {

    // API serviceKey 변수 선언
    @Value("${servicekey}")
    private String serviceKey;
    private final RestTemplate restTemplate;
    private final WebClient webClient;

    public AirQualityService(RestTemplate restTemplate, WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

    // data.go.kr로 부터 미세먼지 정보를 가져옴
    public String getAirQualityDataBasic(String sidoName) throws IOException {
        // serviceKey = System.getenv("app.serviceKey");
        
        // API 요청을 위해 URL 구성
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty"); /*URL*/
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey","UTF-8")).append("=").append(serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml 또는 json*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("sidoName","UTF-8") + "=" + URLEncoder.encode(sidoName, "UTF-8")); /*시도 이름(전국, 서울, 부산, 대구, 인천, 광주, 대전, 울산, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주, 세종)*/
        urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.0", "UTF-8")); /*버전별 상세 결과 참고*/

        // String baseUrl = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";
        // String query = "returnType=json&numOfRows=100&pageNo=1&serviceKey=" + serviceKey + "&sidoName=" + sidoName + "&ver=1.0";

        // URI uri = new URI(baseUrl, query, null);

        // HTTP 연결 후 응답코드 확인
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        // System.out.println("Response code: " + conn.getResponseCode());
        log.info("Response code: {}", conn.getResponseCode());

        // 응답코드가 200이라면 문자 스트림을 이용해서 데이터를 받아옴
        // BufferedReader rd;
        // if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
        //     rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        // } else {
        //     rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        // }

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
        }

        // 버퍼에 저장된 데이터를 하나씩 꺼내 문자열변수에 저장
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        // 작업이 끝나면 버퍼 닫고, HTTP 연결 종료
        rd.close();
        conn.disconnect();

        log.info("apiURL: {}", url);
        log.info("jsonResponse: {}", sb);

        // JSON 유효성 검사

        return sb.toString();
    }

    // getAirQualityDataBasic 개선 - RestTemplate
    public String getAirQualityDataRest(String sidoName) throws IOException, URISyntaxException {

        String encodedSidoName = URLEncoder.encode(sidoName, StandardCharsets.UTF_8);
        String path = "/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";
        
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        
        String uriString = uriBuilderFactory.builder()
                .scheme("http")
                .host("apis.data.go.kr")
                .path(path)
                .queryParam("serviceKey", serviceKey)
                .queryParam("returnType", "json")
                .queryParam("numOfRows", "100")
                .queryParam("pageNo", "1")
                .queryParam("sidoName", encodedSidoName)
                .queryParam("ver", "1.0")
                .build()
                .toString();

        // URI uri = URI.create(baseUrl + query);
        URI uri = URI.create(uriString);

        log.info("api url: {}", uriString);
        // API 요청 보내기
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        log.info("jsonResponse: {}", response.getBody());
        String responseBody = response.getBody();

        return responseBody;
    }

    // getAirQualityDataReactive 개선 - WebClient
    public Mono<String> getAirQualityDataReactive(String sidoName) throws IOException {
        String encodedSidoName = URLEncoder.encode(sidoName, StandardCharsets.UTF_8);
        String path = "/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("apis.data.go.kr")
                        .path(path)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("returnType", "json")
                        .queryParam("numOfRows", "100")
                        .queryParam("pageNo", "1")
                        .queryParam("sidoName", encodedSidoName)
                        .queryParam("ver", "1.0")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("WebClient Error: ", error));
    }


}
