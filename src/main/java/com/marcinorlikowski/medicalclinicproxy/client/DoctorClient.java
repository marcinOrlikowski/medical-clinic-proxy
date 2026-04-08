package com.marcinorlikowski.medicalclinicproxy.client;

import com.marcinorlikowski.medicalclinicproxy.dto.DoctorResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        value = "doctor-client",
        url = "${spring.cloud.openfeign.client.config.medical-clinic.url}",
        fallbackFactory = DoctorClientFallbackFactory.class
)
public interface DoctorClient {
    @GetMapping(value = "/doctors")
    PageDto<DoctorResponse> getByFilters(
            Pageable pageable,
            @RequestParam(required = false) Specialization specialization
    );
}
