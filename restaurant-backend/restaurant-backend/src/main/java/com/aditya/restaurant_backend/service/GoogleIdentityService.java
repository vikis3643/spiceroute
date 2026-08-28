package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class GoogleIdentityService {

    private static final String GOOGLE_JWK_URL =
            "https://www.googleapis.com/oauth2/v3/certs";

    private static final List<String>
            ALLOWED_ISSUERS = List.of(
                    "https://accounts.google.com",
                    "accounts.google.com"
            );

    private final String googleClientId;
    private final NimbusJwtDecoder googleJwtDecoder;

    public GoogleIdentityService(
            @Value("${app.google.client-id}")
            String googleClientId
    ) {
        this.googleClientId =
                googleClientId;

        this.googleJwtDecoder =
                NimbusJwtDecoder
                        .withJwkSetUri(
                                GOOGLE_JWK_URL
                        )
                        .build();

        this.googleJwtDecoder
                .setJwtValidator(
                        new JwtTimestampValidator()
                );
    }

    public GoogleIdentity verify(
            String credential
    ) {
        try {
            Jwt googleJwt =
                    googleJwtDecoder.decode(
                            credential
                    );

            validateIssuer(googleJwt);
            validateAudience(googleJwt);

            String subject =
                    googleJwt.getSubject();

            String email =
                    googleJwt.getClaimAsString(
                            "email"
                    );

            String fullName =
                    googleJwt.getClaimAsString(
                            "name"
                    );

            Boolean emailVerified =
                    googleJwt.getClaim(
                            "email_verified"
                    );

            if (subject == null
                    || subject.isBlank()
                    || email == null
                    || email.isBlank()
                    || !Boolean.TRUE.equals(
                            emailVerified
                    )) {

                throw invalidGoogleCredential();
            }

            if (fullName == null
                    || fullName.isBlank()) {

                fullName = email
                        .substring(
                                0,
                                email.indexOf("@")
                        );
            }

            return new GoogleIdentity(
                    subject,
                    email,
                    fullName
            );
        } catch (JwtException exception) {
            throw invalidGoogleCredential();
        }
    }

    private void validateIssuer(
            Jwt googleJwt
    ) {
        if (googleJwt.getIssuer() == null
                || !ALLOWED_ISSUERS.contains(
                        googleJwt.getIssuer()
                                .toString()
                )) {

            throw invalidGoogleCredential();
        }
    }

    private void validateAudience(
            Jwt googleJwt
    ) {
        if (googleJwt.getAudience() == null
                || !googleJwt
                        .getAudience()
                        .contains(
                                googleClientId
                        )) {

            throw invalidGoogleCredential();
        }
    }

    private BadCredentialsException
            invalidGoogleCredential() {

        return new BadCredentialsException(
                "Google sign-in could not be verified"
        );
    }

    public record GoogleIdentity(
            String subject,
            String email,
            String fullName
    ) {
    }
}