package com.marcinorlikowski.medicalclinicproxy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinorlikowski.medicalclinicproxy.dto.*;
import com.marcinorlikowski.medicalclinicproxy.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppointmentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void getByFilters_ShouldReturnStatusOkAndPageDto_WhenDataCorrect() throws Exception {
        // given
        AppointmentDto appointmentDto = new AppointmentDto(1L, LocalDateTime.now(), LocalDateTime.now(),
                1L, null);
        PageDto<AppointmentDto> pageDto = new PageDto<>(
                List.of(appointmentDto),
                new PageMetadata(0, 20, 1, 1)
        );
        when(appointmentService.getByFilters(any(), any()))
                .thenReturn(pageDto);
        // when & then
        mockMvc.perform(get("/appointments")
                        .param("doctorId", "1")
                        .param("isAvailable", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].doctorId").value(1L));
    }

    @Test
    void assignPatient_shouldReturnStatusOkAndAppointmentDto_WhenDataCorrect() throws Exception {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(appointmentId, patientId);
        AppointmentDto assignedDto = new AppointmentDto(appointmentId, LocalDateTime.now(),
                LocalDateTime.now(), 1L, patientId);
        when(appointmentService.assignPatient(command)).thenReturn(assignedDto);
        // when & then
        mockMvc.perform(patch("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentId))
                .andExpect(jsonPath("$.patientId").value(patientId));
    }

    @Test
    void assignPatient_shouldReturnBadRequest_WhenBodyIncorrect() throws Exception {
        // given & when & then
        mockMvc.perform(patch("/appointments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAppointment_ShouldReturnNoContent_WhenDataCorrect() throws Exception {
        // given
        Long appointmentId = 1L;
        // when & then
        mockMvc.perform(delete("/appointments")
                        .param("appointmentId", "1"))
                .andExpect(status().isNoContent());
        verify(appointmentService).deleteAppointment(appointmentId);
    }

    @Test
    void deleteAppointment_ShouldReturnBadRequest_WhenParamMissing() throws Exception {
        // given & when & then
        mockMvc.perform(delete("/appointments"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(appointmentService);
    }
}
