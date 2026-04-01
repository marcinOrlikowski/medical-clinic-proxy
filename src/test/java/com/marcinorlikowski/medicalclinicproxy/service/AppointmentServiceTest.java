package com.marcinorlikowski.medicalclinicproxy.service;

import com.marcinorlikowski.medicalclinicproxy.client.AppointmentClient;
import com.marcinorlikowski.medicalclinicproxy.dto.*;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicException;
import com.marcinorlikowski.medicalclinicproxy.mapper.AppointmentMapper;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.Mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppointmentServiceTest {
    private AppointmentService appointmentService;
    private AppointmentMapper mapper;
    private AppointmentClient appointmentClient;

    @BeforeEach
    void setup() {
        this.appointmentClient = Mockito.mock(AppointmentClient.class);
        this.mapper = Mappers.getMapper(AppointmentMapper.class);
        this.appointmentService = new AppointmentService(appointmentClient, mapper);
    }

    @Test
    void assignPatient_ShouldReturnAppointmentDto_WhenDataCorrect() {
        // given
        Long appointmentId = 1L;
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(1L, 1L);
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusHours(1);
        AppointmentResponse clientResponse = new AppointmentResponse(appointmentId, startDate,
                endDate, 1L, 1L);
        AppointmentDto expectedDto = new AppointmentDto(appointmentId, startDate, endDate,
                1L, 1L);
        when(appointmentClient.assignPatient(command)).thenReturn(clientResponse);
        // when
        AppointmentDto result = appointmentService.assignPatient(command);
        // then
        assertAll(
                () -> assertEquals(expectedDto, result)
        );
        verify(appointmentClient).assignPatient(command);
    }

    @Test
    void assignPatient_ShouldThrowException_WhenClientFails() {
        // given
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(1L, 1L);
        when(appointmentClient.assignPatient(command))
                .thenThrow(new MedicalClinicException("Patient not found", HttpStatus.NOT_FOUND));

        // when & then
        MedicalClinicException exception = assertThrows(
                MedicalClinicException.class,
                () -> appointmentService.assignPatient(command)
        );
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("Patient not found", exception.getMessage())
        );
    }

    @Test
    void getAvailableBySpecializationAndDate_ShouldReturnPageDto_WhenDataCorrect() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        Specialization spec = Specialization.CARDIOLOGIST;
        LocalDate date = LocalDate.of(2026, 3, 30);
        AppointmentResponse response = new AppointmentResponse(1L, LocalDateTime.now(), LocalDateTime.now(),
                1L, 1L);
        PageMetadata metaData = new PageMetadata(0, 20, 1, 1);
        PageDto<AppointmentResponse> appointmentsPage = new PageDto<>(List.of(response), metaData);
        when(appointmentClient.getAvailableBySpecializationAndDate(pageRequest, spec, date))
                .thenReturn(appointmentsPage);
        // when
        PageDto<AppointmentDto> result = appointmentService.getAvailableBySpecializationAndDate(pageRequest, spec, date);
        // then
        assertAll(
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(1L, result.content().get(0).id()),
                () -> assertEquals(metaData, result.metaData())
        );

    }
}
