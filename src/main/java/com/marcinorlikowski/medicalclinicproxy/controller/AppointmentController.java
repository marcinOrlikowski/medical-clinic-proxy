package com.marcinorlikowski.medicalclinicproxy.controller;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentFilter;
import com.marcinorlikowski.medicalclinicproxy.dto.AssignPatientToAppointmentCommand;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping
    public PageDto<AppointmentDto> getByFilters(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            AppointmentFilter filter
    ) {
        log.info("Received request to get appointments with filters: {}", filter);
        return appointmentService.getByFilters(pageable, filter);
    }

    @PatchMapping
    public AppointmentDto assignPatient(
            @RequestBody AssignPatientToAppointmentCommand command
    ) {
        log.info("Received request to assign patient to appointment. appointmentId: {}, patientId: {}",
                command.appointmentId(), command.patientId());
        return appointmentService.assignPatient(command);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@RequestParam Long appointmentId) {
        log.info("Received request to delete appointment with appointmentId: {}", appointmentId);
        appointmentService.deleteAppointment(appointmentId);
    }

}
