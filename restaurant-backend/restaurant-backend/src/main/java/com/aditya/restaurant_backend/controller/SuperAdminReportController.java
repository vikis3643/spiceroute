package com.aditya.restaurant_backend.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.SuperAdminReportSummaryResponse;
import com.aditya.restaurant_backend.service.SuperAdminReportService;

@RestController
@RequestMapping("/api/super-admin/reports")
public class SuperAdminReportController {

    private final SuperAdminReportService
            superAdminReportService;

    public SuperAdminReportController(
            SuperAdminReportService superAdminReportService
    ) {
        this.superAdminReportService =
                superAdminReportService;
    }

    // ==========================================
    // DATE RANGE PLATFORM REPORT
    // ==========================================

    @GetMapping
    public ResponseEntity<
            SuperAdminReportSummaryResponse
            > getReport(

                    @RequestParam
                    @DateTimeFormat(
                            iso = DateTimeFormat.ISO.DATE
                    )
                    LocalDate startDate,

                    @RequestParam
                    @DateTimeFormat(
                            iso = DateTimeFormat.ISO.DATE
                    )
                    LocalDate endDate
            ) {

        return ResponseEntity.ok(
                superAdminReportService
                        .getReport(
                                startDate,
                                endDate
                        )
        );
    }
}