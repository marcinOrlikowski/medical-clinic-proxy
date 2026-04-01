package com.marcinorlikowski.medicalclinicproxy.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWireMock(port = 8888)
public class DoctorClientTest {
    @Autowired
    private DoctorClient doctorClient;
    @Autowired
    private WireMockServer wireMockServer;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        WireMock.reset();
    }

    @Test
    void getAvailableByDoctorId_shouldReturnParsedPageDto_whenServerReturns200() throws JsonProcessingException {
        // given
        Long doctorId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 20);
        AppointmentDto appointmentDto = new AppointmentDto(1L, LocalDateTime.now(), LocalDateTime.now(),
                1L, null);
        PageDto<AppointmentDto> pageDto = new PageDto<>(
                List.of(appointmentDto),
                new PageMetadata(0, 20, 1, 1)
        );
        stubFor(get(urlPathEqualTo("/appointments/doctor/1/available"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(pageDto))
                        .withStatus(200)));
        //when
        PageDto<AppointmentResponse> result = doctorClient.getAvailableByDoctorId(pageRequest ,doctorId);
        //then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(1L, result.content().get(0).id()),
                () -> assertEquals(1L, result.content().get(0).doctorId())
        );
    }

    @Test
    void getAvailableByDoctorId_ShouldUseFallback_WhenServerReturns503() {
        // given
        Long doctorId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        stubFor(get(urlPathEqualTo("/appointments/doctor/" + doctorId + "/available"))
                .willReturn(aResponse()
                        .withStatus(503)));
        // when
        PageDto<AppointmentResponse> result = doctorClient.getAvailableByDoctorId(pageable, doctorId);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.content().isEmpty())
        );
        verify(3, getRequestedFor(urlPathEqualTo("/appointments/doctor/" + doctorId + "/available")));
    }
}
