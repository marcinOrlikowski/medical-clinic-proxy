package com.marcinorlikowski.medicalclinicproxy.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.marcinorlikowski.medicalclinicproxy.dto.*;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
    void getByFilters_shouldReturnParsedPageDto_whenServerReturns200() throws JsonProcessingException {
        // given
        Specialization specialization = Specialization.valueOf("SURGEON");
        PageRequest pageRequest = PageRequest.of(0, 20);
        DoctorResponse doctorResponse = new DoctorResponse(1L, "email@com.pl", "Sebek",
                "Javowy", Specialization.SURGEON);
        PageDto<DoctorResponse> pageDto = new PageDto<>(
                List.of(doctorResponse),
                new PageMetadata(0, 20, 1, 1)
        );
        stubFor(get(urlPathEqualTo("/doctors"))
                .withQueryParam("page", equalTo("0"))
                .withQueryParam("size", equalTo("20"))
                .withQueryParam("specialization", equalTo("SURGEON"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(pageDto))
                        .withStatus(200)));
        //when
        PageDto<DoctorResponse> result = doctorClient.getByFilters(pageRequest, specialization);
        //then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(1L, result.content().get(0).id()),
                () -> assertEquals(Specialization.SURGEON, result.content().get(0).specialization())
        );
    }

    @Test
    void getByFilters_shouldTriggerFallback_whenServerReturns503() {
        // given
        Specialization specialization = Specialization.SURGEON;
        PageRequest pageRequest = PageRequest.of(0, 20);

        stubFor(get(urlPathEqualTo("/doctors"))
                .willReturn(aResponse()
                        .withStatus(503)));
        // when
        MedicalClinicUnavailableException exception = assertThrows(
                MedicalClinicUnavailableException.class,
                () -> doctorClient.getByFilters(pageRequest, specialization));
        // then
        assertAll(
                () -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus())
        );
        verify(3, getRequestedFor(urlPathEqualTo("/doctors")));
    }
}
