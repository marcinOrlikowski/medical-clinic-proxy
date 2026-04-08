package com.marcinorlikowski.medicalclinicproxy.client;

import com.marcinorlikowski.medicalclinicproxy.dto.*;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicException;
import com.marcinorlikowski.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppointmentClientFallbackFactory implements FallbackFactory<AppointmentClient> {
    @Override
    public AppointmentClient create(Throwable cause) {
        return new AppointmentClient() {
            @Override
            public PageDto<AppointmentResponse> getByFilters(Pageable pageable, AppointmentFilter filter) {
                throw new MedicalClinicUnavailableException(HttpStatus.SERVICE_UNAVAILABLE);
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

            @Override
            public void deleteAppointment(Long appointmentId) {
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
