package com.marcinorlikowski.medicalclinicproxy.service;

import com.marcinorlikowski.medicalclinicproxy.client.PatientClient;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientClient patientClient;
    private final AppointmentMapper mapper;

    public PageDto<AppointmentDto> getAppointmentsForPatient(Pageable pageable, Long patientId) {
        log.info("Getting available appointments for patientId: '{}'", patientId);
        PageDto<AppointmentResponse> page = patientClient.getAppointmentsForPatient(pageable, patientId);
        List<AppointmentDto> appointmentsDto = mapper.toDto(page.content());
        log.info("Returning available appointments for patientId: '{}'", patientId);
        return new PageDto<>(appointmentsDto, page.metaData());
    }
}
