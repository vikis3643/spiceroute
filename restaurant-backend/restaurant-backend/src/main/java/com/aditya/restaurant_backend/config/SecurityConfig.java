package com.aditya.restaurant_backend.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        http
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .cors(
                        Customizer.withDefaults()
                )
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )
                .authorizeHttpRequests(
                        authorize ->
                                authorize

                                        // ========================================
                                        // PUBLIC AUTHENTICATION ENDPOINTS
                                        // ========================================

                                        // Customer authentication
                                        .requestMatchers(
                                                "/api/customer-auth/**"
                                        ).permitAll()

                                        // Restaurant Admin login
                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/api/restaurant-admin/auth/login"
                                        ).permitAll()

                                        // Super Admin login
                                        .requestMatchers(
                                                "/api/super-admin/auth/**"
                                        ).permitAll()

                                        // ========================================
                                        // RESTAURANT ONBOARDING
                                        // ========================================

                                        .requestMatchers(
                                                "/api/restaurant-registration/**"
                                        ).permitAll()

                                        // ========================================
                                        // PUBLIC ENDPOINTS
                                        // ========================================

                                        .requestMatchers(
                                                "/api/health",
                                                "/error"
                                        ).permitAll()

                                        // Public category browsing
                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/categories/**"
                                        ).permitAll()

                                        // Public menu browsing/search
                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/menu-items/**"
                                        ).permitAll()

                                        // ========================================
                                        // PUBLIC MULTI-RESTAURANT MARKETPLACE
                                        // ========================================

                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/marketplace/**"
                                        ).permitAll()

                                        // ========================================
                                        // CUSTOMER ORDER ENDPOINTS
                                        // ========================================

                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/api/orders",
                                                "/api/orders/quote"
                                        ).hasRole(
                                                "CUSTOMER"
                                        )

                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/orders/my-orders"
                                        ).hasRole(
                                                "CUSTOMER"
                                        )

                                        .requestMatchers(
                                                HttpMethod.PATCH,
                                                "/api/orders/*/cancel"
                                        ).hasRole(
                                                "CUSTOMER"
                                        )

                                        // ========================================
                                        // CUSTOMER PROFILE
                                        // ========================================

                                        .requestMatchers(
                                                "/api/customer/**"
                                        ).hasRole(
                                                "CUSTOMER"
                                        )

                                        // ========================================
                                        // CUSTOMER RECOMMENDATIONS
                                        // ========================================

                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/api/recommendations"
                                        ).hasRole(
                                                "CUSTOMER"
                                        )

                                        // ========================================
                                        // CUSTOMER WISHLIST
                                        // ========================================

                                        .requestMatchers(
                                                "/api/wishlist/**"
                                        ).hasRole(
                                                "CUSTOMER"
                                        )

                                        // ========================================
                                        // CUSTOMER REVIEWS
                                        // ========================================

                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/api/reviews/orders/*"
                                        ).hasRole(
                                                "CUSTOMER"
                                        )

                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/reviews/orders/*"
                                        ).hasRole(
                                                "CUSTOMER"
                                        )

                                        // ========================================
                                        // CUSTOMER SUPPORT
                                        // ========================================

                                        .requestMatchers(
                                                "/api/support/tickets/**"
                                        ).hasRole(
                                                "CUSTOMER"
                                        )

                                        // ========================================
                                        // RESTAURANT ADMIN SUPPORT
                                        // ========================================

                                        .requestMatchers(
                                                "/api/support/restaurant-admin/**"
                                        ).hasRole(
                                                "RESTAURANT_ADMIN"
                                        )

                                        // ========================================
                                        // RESTAURANT ADMIN ENDPOINTS
                                        // ========================================

                                        /*
                                         * Login already permitted above.
                                         *
                                         * Everything else under:
                                         *
                                         * /api/restaurant-admin/**
                                         *
                                         * requires RESTAURANT_ADMIN.
                                         */

                                        .requestMatchers(
                                                "/api/restaurant-admin/**"
                                        ).hasRole(
                                                "RESTAURANT_ADMIN"
                                        )

                                        // ========================================
                                        // SUPER ADMIN ENDPOINTS
                                        // ========================================

                                        /*
                                         * Login already permitted above.
                                         *
                                         * Everything else under:
                                         *
                                         * /api/super-admin/**
                                         *
                                         * requires SUPER_ADMIN.
                                         */

                                        .requestMatchers(
                                                "/api/super-admin/**"
                                        ).hasRole(
                                                "SUPER_ADMIN"
                                        )

                                        // ========================================
                                        // EVERYTHING ELSE
                                        // ========================================

                                        .anyRequest()
                                        .authenticated()
                )
                .oauth2ResourceServer(
                        resourceServer ->
                                resourceServer.jwt(
                                        jwt ->
                                                jwt.jwtAuthenticationConverter(
                                                        jwtAuthenticationConverter
                                                )
                                )
                );

        return http.build();
    }

    // ==========================================
    // JWT ROLE CONVERTER
    // ==========================================

    @Bean
    public JwtAuthenticationConverter
            jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter
                authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter
                .setAuthoritiesClaimName(
                        "role"
                );

        authoritiesConverter
                .setAuthorityPrefix(
                        "ROLE_"
                );

        JwtAuthenticationConverter
                authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter
                .setJwtGrantedAuthoritiesConverter(
                        authoritiesConverter
                );

        return authenticationConverter;
    }

    // ==========================================
    // PASSWORD ENCODER
    // ==========================================

    @Bean
    public PasswordEncoder
            passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // ==========================================
    // JWT SECRET KEY
    // ==========================================

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${app.jwt.secret}")
            String secret
    ) {

        return new SecretKeySpec(
                secret.getBytes(
                        StandardCharsets.UTF_8
                ),
                "HmacSHA256"
        );
    }

    // ==========================================
    // JWT ENCODER
    // ==========================================

    @Bean
    public JwtEncoder jwtEncoder(
            SecretKey secretKey
    ) {

        return NimbusJwtEncoder
                .withSecretKey(
                        secretKey
                )
                .algorithm(
                        MacAlgorithm.HS256
                )
                .build();
    }

    // ==========================================
    // JWT DECODER
    // ==========================================

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey secretKey
    ) {

        return NimbusJwtDecoder
                .withSecretKey(
                        secretKey
                )
                .macAlgorithm(
                        MacAlgorithm.HS256
                )
                .build();
    }
}