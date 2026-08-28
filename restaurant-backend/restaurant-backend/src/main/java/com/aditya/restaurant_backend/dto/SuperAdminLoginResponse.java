package com.aditya.restaurant_backend.dto;

public record SuperAdminLoginResponse(

        String token,
        Long superAdminId,
        String fullName,
        String email

) {
}