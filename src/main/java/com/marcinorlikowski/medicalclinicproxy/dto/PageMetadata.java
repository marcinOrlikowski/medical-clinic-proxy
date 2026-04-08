package com.marcinorlikowski.medicalclinicproxy.dto;

public record PageMetadata(
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
