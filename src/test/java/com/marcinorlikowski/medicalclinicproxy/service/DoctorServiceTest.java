package com.marcinorlikowski.medicalclinicproxy.service;

import com.marcinorlikowski.medicalclinicproxy.client.DoctorClient;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageMetadata;
import com.marcinorlikowski.medicalclinicproxy.mapper.AppointmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class DoctorServiceTest {
    private DoctorService doctorService;
    private AppointmentMapper mapper;
    private DoctorClient doctorClient;

    @BeforeEach
    void setup() {
        this.doctorClient = Mockito.mock(DoctorClient.class);
        this.mapper = Mappers.getMapper(AppointmentMapper.class);
        this.doctorService = new DoctorService(doctorClient, mapper);
    }

    @Test
    void getAvailableByDoctorId_ShouldReturnPageDtoWithAppointments_WhenDataCorrect() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        Long doctorId = 1L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusHours(1);
        AppointmentResponse response = new AppointmentResponse(1L, startDate, endDate,
                1L, null);
        PageMetadata metaData = new PageMetadata(0, 20, 1, 1);
        PageDto<AppointmentResponse> clientPage = new PageDto<>(List.of(response), metaData);
        when(doctorClient.getAvailableByDoctorId(pageRequest, doctorId))
                .thenReturn(clientPage);
        // when
        PageDto<AppointmentDto> result = doctorService.getAvailableByDoctorId(pageRequest, doctorId);
        // then
        assertAll(
                () -> assertEquals(1, result.content().size())
        );
        assertEquals(metaData, result.metaData());
    }
}
