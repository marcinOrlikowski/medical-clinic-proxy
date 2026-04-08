package com.marcinorlikowski.medicalclinicproxy.client;

import com.marcinorlikowski.medicalclinicproxy.dto.DoctorResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DoctorClientFallbackFactory implements FallbackFactory<DoctorClient> {
    @Override
    public DoctorClient create(Throwable cause) {
        return new DoctorClient() {
            @Override
            public PageDto<DoctorResponse> getByFilters(Pageable pageable, Specialization specialization) {
                throw new MedicalClinicUnavailableException(HttpStatus.SERVICE_UNAVAILABLE);
            }
        };
    }
}
