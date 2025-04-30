package com.example.zzyzzy.airquality.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
@Service
public class AirQualityService {

    // API serviceKey 변수 선언
    private String serviceKey;

    // data.go.kr로 부터 미세먼지 정보를 가져옴
    public String getAirQualityDataBasic(String sidoName) throws IOException {
        serviceKey = System.getenv("app.serviceKey");

        // API 요청을 위해 URL 구성
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty"); /*URL*/
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey","UTF-8")).append("=").append(serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml 또는 json*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("sidoName","UTF-8") + "=" + URLEncoder.encode(sidoName, "UTF-8")); /*시도 이름(전국, 서울, 부산, 대구, 인천, 광주, 대전, 울산, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주, 세종)*/
        urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.0", "UTF-8")); /*버전별 상세 결과 참고*/

        // HTTP 연결 후 응답코드 확인
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        // 응답코드가 200이라면 문자 스트림을 이용해서 데이터를 받아옴
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
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
    public String getAirQualityDataRest(String sidoName) throws IOException {
        return null;
    }

    // getAirQualityDataRest 개선 - WebClient
    public String getAirQualityDataReactive(String sidoName) throws IOException {
        return null;
    }

}
