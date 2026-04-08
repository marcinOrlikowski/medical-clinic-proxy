package com.marcinorlikowski.medicalclinicproxy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinorlikowski.medicalclinicproxy.dto.*;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import com.marcinorlikowski.medicalclinicproxy.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    void getByFilters_ShouldReturnStatusOkAndPageDto_WhenDataCorrect() throws Exception {
        // given
        Specialization specialization = Specialization.SURGEON;
        DoctorDto doctorDto = new DoctorDto(1L, "email@com.pl", "Sebek",
                "Javowy", Specialization.SURGEON);
        PageDto<DoctorDto> pageDto = new PageDto<>(
                List.of(doctorDto),
                new PageMetadata(0, 20, 1, 1)
        );
        when(doctorService.getByFilters(any(), eq(specialization)))
                .thenReturn(pageDto);
        // when & then
        mockMvc.perform(get("/doctors")
                        .param("specialization", "SURGEON"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }
}
