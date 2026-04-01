package com.marcinorlikowski.medicalclinicproxy.controller;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.service.PatientService;
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
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/{patientId}/appointments")
    public PageDto<AppointmentDto> getAppointmentsForPatient(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @PathVariable Long patientId
    ) {
        log.info("Received request to get appointments for patientId: '{}'", patientId);
        return patientService.getAppointmentsForPatient(pageable, patientId);
    }
}
