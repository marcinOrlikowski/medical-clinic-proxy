package com.marcinorlikowski.medicalclinicproxy.client;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentFilter;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.AssignPatientToAppointmentCommand;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        value = "appointment-client",
        url = "${spring.cloud.openfeign.client.config.medical-clinic.url}",
        fallbackFactory = AppointmentClientFallbackFactory.class
)
public interface AppointmentClient {

    @GetMapping(value = "/appointments")
    PageDto<AppointmentResponse> getByFilters(
            Pageable pageable,
            @SpringQueryMap AppointmentFilter filter
    );

    @PatchMapping(value = "/appointments")
    AppointmentResponse assignPatient(
            @RequestBody AssignPatientToAppointmentCommand command
    );

    @DeleteMapping(value = "/appointments")
    void deleteAppointment(@RequestParam Long appointmentId);
}
