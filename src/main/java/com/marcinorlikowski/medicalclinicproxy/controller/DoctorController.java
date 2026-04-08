package com.marcinorlikowski.medicalclinicproxy.controller;

import com.marcinorlikowski.medicalclinicproxy.dto.DoctorDto;
import com.marcinorlikowski.medicalclinicproxy.dto.PageDto;
import com.marcinorlikowski.medicalclinicproxy.model.Specialization;
import com.marcinorlikowski.medicalclinicproxy.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping
    public PageDto<DoctorDto> getByFilters(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) Specialization specialization
    ) {
        log.info("Received request to get appointments with filters: {}", specialization);
        return doctorService.getByFilters(pageable, specialization);
    }

}
