package com.marcinorlikowski.medicalclinicproxy.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class MedicalClinicException extends RuntimeException {
    private String message;
    private HttpStatus status;

}
