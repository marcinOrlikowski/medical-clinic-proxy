package com.marcinorlikowski.medicalclinicproxy.client;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.AssignPatientToAppointmentCommand;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@FeignClient(
        value = "appointment-client",
        url = "${spring.cloud.openfeign.client.config.medical-clinic.url}",
        fallbackFactory = AppointmentClientFallbackFactory.class
)
public interface AppointmentClient {

    @GetMapping(value = "/appointments/available")
    PageDto<AppointmentResponse> getAvailableBySpecializationAndDate(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam Specialization specialization,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

    @PatchMapping(value = "/appointments/book")
    AppointmentResponse assignPatient(
            @RequestBody AssignPatientToAppointmentCommand command
    );
}
