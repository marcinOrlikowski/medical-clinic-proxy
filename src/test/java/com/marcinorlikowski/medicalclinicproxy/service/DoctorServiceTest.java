package com.marcinorlikowski.medicalclinicproxy.service;

import com.marcinorlikowski.medicalclinicproxy.client.DoctorClient;
import com.marcinorlikowski.medicalclinicproxy.dto.*;
import com.marcinorlikowski.medicalclinicproxy.mapper.DoctorMapper;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class DoctorServiceTest {
    private DoctorService doctorService;
    private DoctorMapper mapper;
    private DoctorClient doctorClient;

    @BeforeEach
    void setup() {
        this.doctorClient = Mockito.mock(DoctorClient.class);
        this.mapper = Mappers.getMapper(DoctorMapper.class);
        this.doctorService = new DoctorService(doctorClient, mapper);
    }

    @Test
    void getByFilters_ShouldReturnPageDtoWithAppointments_WhenDataCorrect() {
        // given
        Specialization specialization = Specialization.SURGEON;
        PageRequest pageRequest = PageRequest.of(0, 20);
        DoctorResponse response = new DoctorResponse(1L, "email@com.pl", "Sebek",
                "Javowy", Specialization.SURGEON);
        PageMetadata metaData = new PageMetadata(0, 20, 1, 1);
        PageDto<DoctorResponse> pageDto = new PageDto<>(List.of(response), metaData);
        when(doctorClient.getByFilters(any(), any()))
                .thenReturn(pageDto);
        // when
        PageDto<DoctorDto> result = doctorService.getByFilters(pageRequest, specialization);
        // then
        assertAll(
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(1L, result.content().get(0).id())
        );
        verify(doctorClient).getByFilters(pageRequest, specialization);
        verifyNoMoreInteractions(doctorClient);
    }

    @Test
    void getByFilters_ShouldReturnEmptyPage_WhenNoAppointmentsMatch() {
        // given
        Specialization specialization = Specialization.SURGEON;
        PageRequest pageRequest = PageRequest.of(0, 20);
        PageMetadata metaData = new PageMetadata(0, 20, 0, 0);
        PageDto<DoctorResponse> emptyPageDto = new PageDto<>(Collections.emptyList(), metaData);
        when(doctorClient.getByFilters(any(), any())).thenReturn(emptyPageDto);
        // when
        PageDto<DoctorDto> result = doctorService.getByFilters(pageRequest, specialization);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.content().isEmpty())
        );
        verify(doctorClient).getByFilters(pageRequest, specialization);
        verifyNoMoreInteractions(doctorClient);
    }
}
