package com.aditya.restaurant_backend.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.expiration-minutes}")
            long expirationMinutes
    ) {

        this.jwtEncoder =
                jwtEncoder;

        this.expirationMinutes =
                expirationMinutes;
    }

    // ==========================================
    // CUSTOMER TOKEN
    // ==========================================

    public String generateCustomerToken(
            String email,
            Long customerId
    ) {

        Instant issuedAt =
                Instant.now();

        Instant expiresAt =
                issuedAt.plus(
                        expirationMinutes,
                        ChronoUnit.MINUTES
                );

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer(
                                "spiceroute-restaurant"
                        )
                        .issuedAt(
                                issuedAt
                        )
                        .expiresAt(
                                expiresAt
                        )
                        .subject(
                                email
                        )
                        .claim(
                                "role",
                                "CUSTOMER"
                        )
                        .claim(
                                "customerId",
                                customerId
                        )
                        .build();

        return encodeToken(
                claims
        );
    }

    // ==========================================
    // RESTAURANT ADMIN TOKEN
    // ==========================================

    public String
            generateRestaurantAdminToken(
                    String email,
                    Long adminId,
                    Long restaurantId
            ) {

        Instant issuedAt =
                Instant.now();

        Instant expiresAt =
                issuedAt.plus(
                        expirationMinutes,
                        ChronoUnit.MINUTES
                );

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer(
                                "spiceroute-restaurant"
                        )
                        .issuedAt(
                                issuedAt
                        )
                        .expiresAt(
                                expiresAt
                        )
                        .subject(
                                email
                        )
                        .claim(
                                "role",
                                "RESTAURANT_ADMIN"
                        )
                        .claim(
                                "adminId",
                                adminId
                        )
                        .claim(
                                "restaurantId",
                                restaurantId
                        )
                        .build();

        return encodeToken(
                claims
        );
    }

    // ==========================================
    // SUPER ADMIN TOKEN
    // ==========================================

    public String
            generateSuperAdminToken(
                    String email,
                    Long superAdminId
            ) {

        Instant issuedAt =
                Instant.now();

        Instant expiresAt =
                issuedAt.plus(
                        expirationMinutes,
                        ChronoUnit.MINUTES
                );

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer(
                                "spiceroute-restaurant"
                        )
                        .issuedAt(
                                issuedAt
                        )
                        .expiresAt(
                                expiresAt
                        )
                        .subject(
                                email
                        )
                        .claim(
                                "role",
                                "SUPER_ADMIN"
                        )
                        .claim(
                                "superAdminId",
                                superAdminId
                        )
                        .build();

        return encodeToken(
                claims
        );
    }

    // ==========================================
    // JWT ENCODER
    // ==========================================

    private String encodeToken(
            JwtClaimsSet claims
    ) {

        JwsHeader header =
                JwsHeader
                        .with(
                                MacAlgorithm.HS256
                        )
                        .build();

        JwtEncoderParameters parameters =
                JwtEncoderParameters.from(
                        header,
                        claims
                );

        return jwtEncoder
                .encode(
                        parameters
                )
                .getTokenValue();
    }
}