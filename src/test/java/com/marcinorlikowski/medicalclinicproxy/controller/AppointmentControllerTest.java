package com.marcinorlikowski.medicalclinicproxy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.AssignPatientToAppointmentCommand;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageMetadata;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import com.marcinorlikowski.medicalclinicproxy.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    void getAvailableBySpecializationAndDate_ShouldReturnStatusOkAndPageDto_WhenDataCorrect() throws Exception {
        // given
        Specialization spec = Specialization.CARDIOLOGIST;
        LocalDate date = LocalDate.of(2026, 3, 30);
        PageDto<AppointmentDto> emptyPage = new PageDto<>(List.of(), new PageMetadata(0, 20, 0, 0));

        when(appointmentService.getAvailableBySpecializationAndDate(any(), eq(spec), eq(date)))
                .thenReturn(emptyPage);
        // when & then
        mockMvc.perform(get("/appointments/available")
                        .param("specialization", spec.name())
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
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
        mockMvc.perform(patch("/appointments/book", appointmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentId))
                .andExpect(jsonPath("$.patientId").value(patientId));
    }

    @Test
    void getAvailableBySpecializationAndDate_ShouldReturnBadRequest_WhenSpecializationInvalid() throws Exception {
        // given
        String spec = "CHEF";
        LocalDate date = LocalDate.of(2026, 3, 30);
        // when & then
        mockMvc.perform(get("/appointments/available")
                        .param("specialization", spec)
                        .param("date", date.toString()))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(appointmentService);
    }

    @Test
    void getAvailableBySpecializationAndDate_ShouldReturnBadRequest_WhenDateInvalid() throws Exception {
        // given
        Specialization spec = Specialization.CARDIOLOGIST;
        // when & then
        mockMvc.perform(get("/appointments/available")
                        .param("specialization", spec.name()))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(appointmentService);
    }
}
