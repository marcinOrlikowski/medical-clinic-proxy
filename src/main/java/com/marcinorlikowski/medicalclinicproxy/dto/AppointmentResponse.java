package com.marcinorlikowski.medicalclinicproxy.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long doctorId,
        Long patientId
) {
}
