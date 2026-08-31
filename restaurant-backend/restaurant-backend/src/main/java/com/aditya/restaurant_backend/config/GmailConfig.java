package com.aditya.restaurant_backend.config;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;

@Configuration
public class GmailConfig {

    private static final String APPLICATION_NAME = "SpiceRoute";

    @Value("${GOOGLE_CLIENT_ID:}")
    private String clientId;

    @Value("${GOOGLE_CLIENT_SECRET:}")
    private String clientSecret;

    @Value("${GOOGLE_REFRESH_TOKEN:}")
    private String refreshToken;

    @Bean
    public Gmail gmail()
            throws GeneralSecurityException, IOException {

        if (clientId == null || clientId.isBlank()) {
            throw new IllegalStateException(
                    "GOOGLE_CLIENT_ID is not configured"
            );
        }

        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException(
                    "GOOGLE_CLIENT_SECRET is not configured"
            );
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalStateException(
                    "GOOGLE_REFRESH_TOKEN is not configured"
            );
        }

        UserCredentials credentials =
                UserCredentials.newBuilder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .setRefreshToken(refreshToken)
                        .build();

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        )
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}