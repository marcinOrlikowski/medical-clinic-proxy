package com.marcinorlikowski.medicalclinicproxy.service;

import com.marcinorlikowski.medicalclinicproxy.client.DoctorClient;
import com.marcinorlikowski.medicalclinicproxy.dto.*;
import com.marcinorlikowski.medicalclinicproxy.mapper.DoctorMapper;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
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
    private final DoctorMapper mapper;

    public PageDto<DoctorDto> getByFilters(Pageable pageable, Specialization specialization) {
        log.info("Getting doctors with filters: specialization: '{}'", specialization);
        PageDto<DoctorResponse> doctors = doctorClient.getByFilters(pageable, specialization);
        List<DoctorDto> doctorsDto = mapper.toDto(doctors.content());
        log.info("Returning doctors with filters: specialization: '{}'", specialization);
        return new PageDto<>(doctorsDto, doctors.metaData());
    }
}
