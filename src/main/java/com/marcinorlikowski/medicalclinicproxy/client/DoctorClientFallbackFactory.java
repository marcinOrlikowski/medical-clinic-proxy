package com.marcinorlikowski.medicalclinicproxy.client;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class DoctorClientFallbackFactory implements FallbackFactory<DoctorClient> {
    @Override
    public DoctorClient create(Throwable cause) {
        return new DoctorClient() {
            @Override
            public PageDto<AppointmentResponse> getAvailableByDoctorId(Pageable pageable, Long doctorId) {
                log.warn("Fallback triggered for getAvailableByDoctorId");
                return new PageDto<>(
                        Collections.emptyList(),
                        new PageMetadata(0, 0, 0, 0)
                );
            }
        };
    }
}
