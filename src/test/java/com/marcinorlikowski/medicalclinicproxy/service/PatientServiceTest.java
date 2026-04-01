package com.marcinorlikowski.medicalclinicproxy.service;

import com.marcinorlikowski.medicalclinicproxy.client.PatientClient;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageMetadata;
import com.marcinorlikowski.medicalclinicproxy.mapper.AppointmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    private PatientClient patientClient;
    private AppointmentMapper mapper;
    private PatientService patientService;

    @BeforeEach
    void setup() {
        this.patientClient = Mockito.mock(PatientClient.class);
        this.mapper = Mappers.getMapper(AppointmentMapper.class);
        this.patientService = new PatientService(patientClient, mapper);
    }

    @Test
    void getAppointmentsForPatient_ShouldReturnPageDto_WhenDataCorrect() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        Long patientId = 1L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusHours(1);
        AppointmentResponse response = new AppointmentResponse(1L, startDate, endDate,
                1L, 1L);
        PageMetadata metaData = new PageMetadata(0, 20, 1, 1);
        PageDto<AppointmentResponse> clientPage = new PageDto<>(List.of(response), metaData);
        when(patientClient.getAppointmentsForPatient(pageRequest, patientId)).thenReturn(clientPage);
        // when
        PageDto<AppointmentDto> result = patientService.getAppointmentsForPatient(pageRequest, patientId);
        // then
        assertAll(
                () -> assertEquals(1, result.content().size())
        );
    }
}
