package com.marcinorlikowski.medicalclinicproxy.client;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.AssignPatientToAppointmentCommand;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageMetadata;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicException;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;

@Slf4j
@Component
public class AppointmentClientFallbackFactory implements FallbackFactory<AppointmentClient> {
    @Override
    public AppointmentClient create(Throwable cause) {
        return new AppointmentClient() {
            @Override
            public PageDto<AppointmentResponse> getAvailableBySpecializationAndDate(
                    Pageable pageable, Specialization specialization, LocalDate date) {
                log.warn("Fallback triggered for getAvailableBySpecializationAndDate");
                return new PageDto<>(
                        Collections.emptyList(),
                        new PageMetadata(0, 0, 0, 0)
                );
            }

            @Override
            public AppointmentResponse assignPatient(AssignPatientToAppointmentCommand command) {
                if (cause instanceof MedicalClinicException medicalException) {
                    log.error("API error with status: '{}', message: '{}'",
                            medicalException.getStatus(), medicalException.getMessage());
                    throw medicalException;
                }
                log.error("Unknown error: {}", cause.getMessage());
                throw new MedicalClinicUnavailableException(HttpStatus.SERVICE_UNAVAILABLE);
            }
        };
    }
}
