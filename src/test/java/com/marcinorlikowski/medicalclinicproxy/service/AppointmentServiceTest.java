package com.marcinorlikowski.medicalclinicproxy.service;

import com.marcinorlikowski.medicalclinicproxy.client.AppointmentClient;
import com.marcinorlikowski.medicalclinicproxy.dto.*;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicException;
import com.marcinorlikowski.medicalclinicproxy.mapper.AppointmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.Mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
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
    void getByFilters_ShouldReturnPageDtoWithAppointments_WhenDataCorrect() {
        // given
        AppointmentFilter filter = new AppointmentFilter();
        filter.setDoctorId(1L);
        filter.setIsAvailable(true);
        PageRequest pageRequest = PageRequest.of(0, 20);
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusHours(1);
        AppointmentResponse response = new AppointmentResponse(1L, startDate, endDate,
                1L, null);
        PageMetadata metaData = new PageMetadata(0, 20, 1, 1);
        PageDto<AppointmentResponse> pageDto = new PageDto<>(List.of(response), metaData);
        when(appointmentClient.getByFilters(any(), any()))
                .thenReturn(pageDto);
        // when
        PageDto<AppointmentDto> result = appointmentService.getByFilters(pageRequest, filter);
        // then
        assertAll(
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(1L, result.content().get(0).doctorId()),
                () -> assertNull(result.content().get(0).patientId())
        );
        verify(appointmentClient).getByFilters(pageRequest, filter);
        verifyNoMoreInteractions(appointmentClient);
    }

    @Test
    void getByFilters_ShouldReturnEmptyPage_WhenNoAppointmentsMatch() {
        // given
        AppointmentFilter filter = new AppointmentFilter();
        PageRequest pageRequest = PageRequest.of(0, 20);
        PageMetadata metaData = new PageMetadata(0, 20, 0, 0);
        PageDto<AppointmentResponse> emptyPageDto = new PageDto<>(Collections.emptyList(), metaData);
        when(appointmentClient.getByFilters(any(), any())).thenReturn(emptyPageDto);
        // when
        PageDto<AppointmentDto> result = appointmentService.getByFilters(pageRequest, filter);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.content().isEmpty())
        );
        verify(appointmentClient).getByFilters(pageRequest, filter);
        verifyNoMoreInteractions(appointmentClient);
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
        verifyNoMoreInteractions(appointmentClient);
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
        verify(appointmentClient).assignPatient(command);
        verifyNoMoreInteractions(appointmentClient);
    }

    @Test
    void deleteAppointment_ShouldCallClient_WhenDataCorrect() {
        // given
        Long appointmentId = 1L;
        // when & then
        appointmentService.deleteAppointment(appointmentId);
        verify(appointmentClient).deleteAppointment(appointmentId);
        verifyNoMoreInteractions(appointmentClient);
    }
}
