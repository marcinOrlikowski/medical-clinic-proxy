package com.marcinorlikowski.medicalclinicproxy.controller;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.AssignPatientToAppointmentCommand;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import com.marcinorlikowski.medicalclinicproxy.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping("/available")
    public PageDto<AppointmentDto> getAvailableBySpecializationAndDate(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam Specialization specialization,
            @RequestParam LocalDate date
    ) {
        log.info("Received request to get available appointments. specialization={}, date={}", specialization, date);
        return appointmentService.getAvailableBySpecializationAndDate(pageable, specialization, date);

    }

    @PatchMapping("/book")
    public AppointmentDto assignPatient(
            @RequestBody AssignPatientToAppointmentCommand command
    ) {
        log.info("Received request to assign patient to appointment. appointmentId: {}, patientId: {}",
                command.appointmentId(), command.patientId());
        return appointmentService.assignPatient(command);
    }

}
