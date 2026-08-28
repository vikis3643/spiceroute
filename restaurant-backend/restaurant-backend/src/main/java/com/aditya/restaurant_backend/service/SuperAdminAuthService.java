package com.aditya.restaurant_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.SuperAdminLoginRequest;
import com.aditya.restaurant_backend.dto.SuperAdminLoginResponse;
import com.aditya.restaurant_backend.entity.SuperAdmin;
import com.aditya.restaurant_backend.repository.SuperAdminRepository;

@Service
public class SuperAdminAuthService {

    private final SuperAdminRepository
            superAdminRepository;

    private final PasswordEncoder
            passwordEncoder;

    private final JwtService
            jwtService;

    public SuperAdminAuthService(
            SuperAdminRepository superAdminRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.superAdminRepository =
                superAdminRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtService =
                jwtService;
    }

    public SuperAdminLoginResponse login(
            SuperAdminLoginRequest request
    ) {

        SuperAdmin superAdmin =
                superAdminRepository
                        .findByEmailIgnoreCase(
                                request.email()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Invalid email or password"
                                )
                        );

        if (!superAdmin.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Super Admin account is inactive"
            );
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.password(),
                        superAdmin.getPasswordHash()
                );

        if (!passwordMatches) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        String token =
                jwtService
                        .generateSuperAdminToken(
                                superAdmin.getEmail(),
                                superAdmin.getId()
                        );

        return new SuperAdminLoginResponse(
                token,
                superAdmin.getId(),
                superAdmin.getFullName(),
                superAdmin.getEmail()
        );
    }
}