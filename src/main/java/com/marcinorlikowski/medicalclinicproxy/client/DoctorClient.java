package com.marcinorlikowski.medicalclinicproxy.client;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        value = "doctor-client",
        url = "${spring.cloud.openfeign.client.config.medical-clinic.url}",
        fallbackFactory = DoctorClientFallbackFactory.class
)
public interface DoctorClient {
    @GetMapping(value = "/appointments/doctor/{doctorId}/available")
    PageDto<AppointmentResponse> getAvailableByDoctorId(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @PathVariable("doctorId") Long doctorId
    );
}
