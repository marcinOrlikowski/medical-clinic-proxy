package com.marcinorlikowski.medicalclinicproxy.dto;

import java.time.LocalDateTime;

public record AppointmentDto(
        Long id,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long doctorId,
        Long patientId
) {
}
