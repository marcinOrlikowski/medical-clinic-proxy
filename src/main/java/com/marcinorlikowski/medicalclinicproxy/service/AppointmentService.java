package com.marcinorlikowski.medicalclinicproxy.service;

import com.marcinorlikowski.medicalclinicproxy.client.AppointmentClient;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.AssignPatientToAppointmentCommand;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.mapper.AppointmentMapper;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentClient appointmentClient;
    private final AppointmentMapper mapper;

    public AppointmentDto assignPatient(AssignPatientToAppointmentCommand command) {
        log.info("Assigning patient to appointment. appointmentId '{}', patientId '{}'",
                command.appointmentId(), command.patientId());
        AppointmentResponse appointment = appointmentClient.assignPatient(command);
        log.info("Patient successfully assigned for appointmentId '{}'", command.appointmentId());
        return mapper.toDto(appointment);
    }

    public PageDto<AppointmentDto> getAvailableBySpecializationAndDate(
            Pageable pageable,
            Specialization specialization,
            LocalDate date
    ) {
        log.info("Getting available appointments for specialization: '{}', date: '{}'", specialization, date);
        PageDto<AppointmentResponse> page = appointmentClient.getAvailableBySpecializationAndDate(pageable,
                specialization, date);
        List<AppointmentDto> appointmentsDto = mapper.toDto(page.content());
        log.info("Returning available appointments for specialization: '{}', date: '{}'", specialization, date);
        return new PageDto<>(appointmentsDto, page.metaData());
    }
}
