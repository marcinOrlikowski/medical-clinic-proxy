package com.marcinorlikowski.medicalclinicproxy.dto;

import com.marcinorlikowski.medicalclinicproxy.model.Specialization;

public record DoctorResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        Specialization specialization) {
}
