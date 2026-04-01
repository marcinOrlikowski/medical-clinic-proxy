package com.marcinorlikowski.medicalclinicproxy.exceptions;

import com.marcinorlikowski.medicalclinicproxy.dto.ErrorDto;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            MedicalClinicException.class
    })
    public ResponseEntity<ErrorDto> handleMedicalClinicExceptions(MedicalClinicException ex) {
        ErrorDto errorDto = new ErrorDto(
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        log.error(ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(errorDto);
    }

    @ExceptionHandler({
            FeignException.class
    })
    public ResponseEntity<ErrorDto> handleFeignException(FeignException ex) {
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ErrorDto errorDto = new ErrorDto(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        log.error("Github API error: {}", ex.getMessage());
        return new ResponseEntity<>(errorDto, status);
    }
}
