package com.marcinorlikowski.medicalclinicproxy.service;

import com.marcinorlikowski.medicalclinicproxy.client.AppointmentClient;
import com.marcinorlikowski.medicalclinicproxy.dto.*;
import com.marcinorlikowski.medicalclinicproxy.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentClient appointmentClient;
    private final AppointmentMapper mapper;

    public PageDto<AppointmentDto> getByFilters(
            Pageable pageable,
            AppointmentFilter filter
    ) {
        PageDto<AppointmentResponse> appointments = appointmentClient.getByFilters(
                pageable,
                filter
        );
        List<AppointmentDto> appointmentsDto = mapper.toDto(appointments.content());
        return new PageDto<>(appointmentsDto, appointments.metaData());
    }

    public AppointmentDto assignPatient(AssignPatientToAppointmentCommand command) {
        log.info("Assigning patient to appointment. appointmentId '{}', patientId '{}'",
                command.appointmentId(), command.patientId());
        AppointmentResponse appointment = appointmentClient.assignPatient(command);
        log.info("Patient successfully assigned for appointmentId '{}'", command.appointmentId());
        return mapper.toDto(appointment);
    }

    public void deleteAppointment(Long appointmentId) {
        log.info("Deleting appointment with appointmentId: '{}'", appointmentId);
        appointmentClient.deleteAppointment(appointmentId);
        log.info("Appointment with appointmentId: '{}' successfully removed", appointmentId);
    }
}
