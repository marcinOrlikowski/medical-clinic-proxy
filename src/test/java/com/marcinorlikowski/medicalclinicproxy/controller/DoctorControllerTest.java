package com.marcinorlikowski.medicalclinicproxy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageMetadata;
import com.marcinorlikowski.medicalclinicproxy.service.DoctorService;
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
public class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private DoctorService doctorService;


    @Test
    void getAvailableByDoctorId_shouldReturnStatusOkAndPageDto_WhenDataCorrect() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        Long doctorId = 1L;
        AppointmentDto appointmentDto = new AppointmentDto(1L, LocalDateTime.now(), LocalDateTime.now(), doctorId, null);
        PageDto<AppointmentDto> pageDto = new PageDto<>(List.of(appointmentDto), new PageMetadata(0, 20, 1, 1));
        when(doctorService.getAvailableByDoctorId(any(), eq(doctorId))).thenReturn(pageDto);
        // when & then
        mockMvc.perform(get("/doctors/1/appointments/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].doctorId").value(doctorId));
    }
}
