package com.marcinorlikowski.medicalclinicproxy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageMetadata;
import com.marcinorlikowski.medicalclinicproxy.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    @Test
    void getAppointmentsForPatient_shouldReturnStatusOkAndPageDto_WhenDataCorrect() throws Exception {
        // given
        Long patientId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 20);
        AppointmentDto appointmentDto = new AppointmentDto(1L, LocalDateTime.now(), LocalDateTime.now(),
                5L, patientId);
        PageDto<AppointmentDto> pageDto = new PageDto<>(
                List.of(appointmentDto),
                new PageMetadata(0, 20, 1, 1)
        );
        when(patientService.getAppointmentsForPatient(any(), eq(patientId))).thenReturn(pageDto);
        // when & then
        mockMvc.perform(get("/patients/{patientId}/appointments", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].patientId").value(patientId));
    }
}
