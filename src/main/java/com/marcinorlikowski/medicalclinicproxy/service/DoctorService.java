package com.marcinorlikowski.medicalclinicproxy.service;

import com.marcinorlikowski.medicalclinicproxy.client.DoctorClient;
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
public class DoctorService {
    private final DoctorClient doctorClient;
    private final AppointmentMapper mapper;

    public PageDto<AppointmentDto> getAvailableByDoctorId(Pageable pageable, Long doctorId) {
        log.info("Getting available appointments for doctorId: '{}'", doctorId);
        PageDto<AppointmentResponse> page = doctorClient.getAvailableByDoctorId(pageable, doctorId);
        List<AppointmentDto> appointmentsDto = mapper.toDto(page.content());
        log.info("Returning available appointments for doctorId: '{}'", doctorId);
        return new PageDto<>(appointmentsDto, page.metaData());
    }
}
