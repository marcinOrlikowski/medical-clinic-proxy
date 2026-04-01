package com.marcinorlikowski.medicalclinicproxy.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.AssignPatientToAppointmentCommand;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicException;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

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

    @Test
    void assignPatient_ShouldReturnAppointmentResponse_WhenDataCorrect() throws Exception {
        // given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusHours(1);
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(1L, 1L);
        AppointmentResponse appointmentResponse = new AppointmentResponse(1L, startDate, endDate, 1L, 1L);
        stubFor(patch(urlPathEqualTo("/appointments/book"))
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
        stubFor(patch(urlPathEqualTo("/appointments/book"))
                .willReturn(aResponse().withStatus(503)));
        // when & then
        assertThrows(
                MedicalClinicUnavailableException.class,
                () -> appointmentClient.assignPatient(command)
        );
        verify(3, patchRequestedFor(urlEqualTo("/appointments/book")));
    }

    @Test
    void assignPatient_ShouldMedicalClinicExceptionThrow_WhenServerReturns404() throws Exception {
        // given
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(1L, 1L);
        stubFor(patch(urlPathEqualTo("/appointments/book"))
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
}

