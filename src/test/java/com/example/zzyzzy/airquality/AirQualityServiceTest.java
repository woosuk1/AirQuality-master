package com.example.zzyzzy.airquality;

import com.example.zzyzzy.airquality.service.AirQualityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AirQualityServiceTest {

    private AirQualityService airQualityService;

    @BeforeEach
    public void setUp() {
        airQualityService = new AirQualityService();
    }

    @Test
    @DisplayName("testAirQualityServiceBasic")
    public void testAirQualityServiceBasic() throws IOException {
        // 테스트 실행 (메서드 호출)
        String result = airQualityService.getAirQualityDataBasic("서울");

        // 결과 검증
        assertNotNull(result);  // 응답이 Null인지 여부 확인
        assertFalse(result.isEmpty());  // 응답이 비었는지 여부 확인

        // JSON응답이 유효한지 여부 확인
        // 즉, 올바른 JSON 형태의 데이터인지 확인
        assertDoesNotThrow(()
                -> new ObjectMapper().readTree(result));
    }

}
