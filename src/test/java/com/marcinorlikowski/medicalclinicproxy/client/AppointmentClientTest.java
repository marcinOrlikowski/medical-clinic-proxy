package com.marcinorlikowski.medicalclinicproxy.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.marcinorlikowski.medicalclinicproxy.dto.*;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicException;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWireMock(port = 8888)
public class AppointmentClientTest {
    @Autowired
    private AppointmentClient appointmentClient;
    @Autowired
    private WireMockServer wireMockServer;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        WireMock.reset();
    }

    @Test
    void assignPatient_ShouldReturnAppointmentResponse_WhenDataCorrect() throws Exception {
        // given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusHours(1);
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(1L, 1L);
        AppointmentResponse appointmentResponse = new AppointmentResponse(1L, startDate, endDate, 1L, 1L);
        stubFor(patch(urlPathEqualTo("/appointments"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(command)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(appointmentResponse))));
        // when
        AppointmentResponse result = appointmentClient.assignPatient(command);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(1L, result.doctorId()),
                () -> assertEquals(1L, result.patientId()),
                () -> assertEquals(startDate, result.startDate()),
                () -> assertEquals(endDate, result.endDate())
        );
    }

    @Test
    void assignPatient_ShouldRetryRequest_WhenServerReturns503() {
        // given
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(1L, 1L);
        stubFor(patch(urlPathEqualTo("/appointments"))
                .willReturn(aResponse().withStatus(503)));
        // when & then
        assertThrows(
                MedicalClinicUnavailableException.class,
                () -> appointmentClient.assignPatient(command)
        );
        verify(3, patchRequestedFor(urlEqualTo("/appointments")));
    }

    @Test
    void assignPatient_ShouldThrowMedicalClinicException_WhenServerReturns404() throws Exception {
        // given
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(1L, 1L);
        stubFor(patch(urlPathEqualTo("/appointments"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(command)))
                .willReturn(aResponse()
                        .withStatus(404)));
        // when & then
        MedicalClinicException exception = assertThrows(
                MedicalClinicException.class,
                () -> appointmentClient.assignPatient(command)
        );
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void getByFilters_shouldReturnParsedPageDto_whenServerReturns200() throws JsonProcessingException {
        // given
        AppointmentFilter filter = new AppointmentFilter();
        filter.setDoctorId(1L);
        filter.setIsAvailable(true);
        PageRequest pageRequest = PageRequest.of(0, 20);
        AppointmentDto appointmentDto = new AppointmentDto(1L, LocalDateTime.now(), LocalDateTime.now(),
                1L, null);
        PageDto<AppointmentDto> pageDto = new PageDto<>(
                List.of(appointmentDto),
                new PageMetadata(0, 20, 1, 1)
        );
        stubFor(get(urlPathEqualTo("/appointments"))
                .withQueryParam("page", equalTo("0"))
                .withQueryParam("size", equalTo("20"))
                .withQueryParam("doctorId", equalTo("1"))
                .withQueryParam("isAvailable", equalTo("true"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(pageDto))
                        .withStatus(200)));
        //when
        PageDto<AppointmentResponse> result = appointmentClient.getByFilters(pageRequest, filter);
        //then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(1L, result.content().get(0).id()),
                () -> assertEquals(1L, result.content().get(0).doctorId())
        );
    }

    @Test
    void getByFilters_shouldTriggerFallback_whenServerReturns503() {
        // given
        AppointmentFilter filter = new AppointmentFilter();
        filter.setDoctorId(1L);
        PageRequest pageRequest = PageRequest.of(0, 20);

        stubFor(get(urlPathEqualTo("/appointments"))
                .willReturn(aResponse()
                        .withStatus(503)));
        // when
        MedicalClinicUnavailableException exception = assertThrows(
                MedicalClinicUnavailableException.class,
                () -> appointmentClient.getByFilters(pageRequest, filter));
        // then
        assertAll(
                () -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus())
        );
        verify(3, getRequestedFor(urlPathEqualTo("/appointments")));
    }

    @Test
    void deleteAppointment_ShouldReturn204NoContent_WhenDataCorrect() {
        // given
        stubFor(delete(urlPathEqualTo("/appointments"))
                .withQueryParam("appointmentId", equalTo("1"))
                .willReturn(aResponse()
                        .withStatus(204)));
        // when & then
        appointmentClient.deleteAppointment(1L);

        verify(1, deleteRequestedFor(urlPathEqualTo("/appointments"))
                .withQueryParam("appointmentId", equalTo("1")));
    }

    @Test
    void deleteAppointment_ShouldThrowMedicalClinicException_WhenServerReturns404() throws Exception {
        // given
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(1L, 1L);
        stubFor(patch(urlPathEqualTo("/appointments"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(command)))
                .willReturn(aResponse()
                        .withStatus(404)));
        // when & then
        MedicalClinicException exception = assertThrows(
                MedicalClinicException.class,
                () -> appointmentClient.assignPatient(command)
        );
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void deleteAppointment_ShouldTriggerFallback_WhenServerReturns503() {
        // given
        stubFor(delete(urlPathEqualTo("/appointments"))
                .withQueryParam("appointmentId", equalTo("1"))
                .willReturn(aResponse()
                        .withStatus(503)));
        // when & then
        MedicalClinicUnavailableException exception = assertThrows(
                MedicalClinicUnavailableException.class,
                () -> appointmentClient.deleteAppointment(1L)
        );
        assertAll(
                () -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus())
        );
        verify(3, deleteRequestedFor(urlPathEqualTo("/appointments"))
                .withQueryParam("appointmentId", equalTo("1")));
    }
}

