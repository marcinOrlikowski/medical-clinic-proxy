package com.marcinorlikowski.medicalclinicproxy.dto;

import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentFilter {
    private Long doctorId;
    private Long patientId;
    private Specialization specialization;
    private LocalDate date;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isAvailable;
}
