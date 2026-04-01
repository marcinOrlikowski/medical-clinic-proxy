package com.marcinorlikowski.medicalclinicproxy.dto;

public record AssignPatientToAppointmentCommand(
        Long appointmentId,
        Long patientId
) {
}
