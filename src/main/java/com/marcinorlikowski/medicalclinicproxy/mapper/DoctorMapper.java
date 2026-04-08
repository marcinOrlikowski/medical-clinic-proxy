package com.marcinorlikowski.medicalclinicproxy.mapper;

import com.marcinorlikowski.medicalclinicproxy.dto.DoctorDto;
import com.marcinorlikowski.medicalclinicproxy.dto.DoctorResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    DoctorDto toDto(DoctorResponse response);

    List<DoctorDto> toDto(List<DoctorResponse> responses);
}
