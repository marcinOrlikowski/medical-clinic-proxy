package com.marcinorlikowski.medicalclinicproxy.controller;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/{doctorId}/appointments/available")
    public PageDto<AppointmentDto> getAvailableByDoctorId(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @PathVariable Long doctorId
    ) {
        log.info("Received request to get available appointments for doctorId: '{}'", doctorId);
        return doctorService.getAvailableByDoctorId(pageable, doctorId);
    }
}
