package com.marcinorlikowski.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class MedicalClinicUnavailableException extends MedicalClinicException {

    public MedicalClinicUnavailableException(HttpStatus status) {
        super("Medical Clinic API is not available", status);
    }
}
