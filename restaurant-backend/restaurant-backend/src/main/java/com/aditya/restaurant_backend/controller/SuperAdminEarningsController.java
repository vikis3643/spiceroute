package com.aditya.restaurant_backend.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.SuperAdminEarningsDateRangeResponse;
import com.aditya.restaurant_backend.dto.SuperAdminPlatformEarningsResponse;
import com.aditya.restaurant_backend.service.SuperAdminEarningsService;

@RestController
@RequestMapping("/api/super-admin/earnings")
public class SuperAdminEarningsController {

    private final SuperAdminEarningsService
            superAdminEarningsService;

    public SuperAdminEarningsController(
            SuperAdminEarningsService superAdminEarningsService
    ) {

        this.superAdminEarningsService =
                superAdminEarningsService;
    }

    // ==========================================
    // PLATFORM-WIDE EARNINGS
    // ==========================================

    @GetMapping
    public ResponseEntity<
            SuperAdminPlatformEarningsResponse
            > getPlatformEarnings() {

        return ResponseEntity.ok(
                superAdminEarningsService
                        .getPlatformEarnings()
        );
    }

    // ==========================================
    // DATE-RANGE EARNINGS
    // ==========================================

    @GetMapping("/range")
    public ResponseEntity<
            SuperAdminEarningsDateRangeResponse
            > getEarningsByDateRange(

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
                superAdminEarningsService
                        .getEarningsByDateRange(
                                startDate,
                                endDate
                        )
        );
    }
}