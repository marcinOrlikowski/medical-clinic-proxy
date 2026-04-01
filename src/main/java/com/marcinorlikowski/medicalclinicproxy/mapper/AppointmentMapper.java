package com.marcinorlikowski.medicalclinicproxy.mapper;

import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinicproxy.dto.AppointmentResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    AppointmentDto toDto(AppointmentResponse response);

    List<AppointmentDto> toDto(List<AppointmentResponse> responses);
}
