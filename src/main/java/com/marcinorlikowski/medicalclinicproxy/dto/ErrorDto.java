package com.marcinorlikowski.medicalclinicproxy.dto;

import java.time.LocalDateTime;

public record ErrorDto(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
}
