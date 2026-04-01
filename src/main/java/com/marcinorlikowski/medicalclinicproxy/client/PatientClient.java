package com.marcinorlikowski.medicalclinicproxy.client;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        value = "patient-client",
        url = "${spring.cloud.openfeign.client.config.medical-clinic.url}",
        fallbackFactory = PatientClientFallbackFactory.class
)
public interface PatientClient {
    @GetMapping(value = "/appointments/patient/{patientId}")
    PageDto<AppointmentResponse> getAppointmentsForPatient(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @PathVariable("patientId") Long patientId
    );
}
