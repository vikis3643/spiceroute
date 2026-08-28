package com.aditya.restaurant_backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.CustomerAccount;

public interface CustomerAccountRepository
        extends JpaRepository<CustomerAccount, Long> {

    // ==========================================
    // CUSTOMER LOOKUP
    // ==========================================

    Optional<CustomerAccount>
            findByEmailIgnoreCase(
                    String email
            );

    Optional<CustomerAccount>
            findByPhone(
                    String phone
            );

    Optional<CustomerAccount>
            findByGoogleSubject(
                    String googleSubject
            );

    Optional<CustomerAccount>
            findByEmailIgnoreCaseOrPhone(
                    String email,
                    String phone
            );

    // ==========================================
    // DUPLICATE CHECKS
    // ==========================================

    boolean existsByEmailIgnoreCase(
            String email
    );

    boolean existsByPhone(
            String phone
    );

    // ==========================================
    // SUPER ADMIN CUSTOMER REPORTS
    // ==========================================

    long countByActiveTrue();

    long countByActiveFalse();

    long countByEmailVerifiedTrue();

    long countByEmailVerifiedFalse();

    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime start,
            LocalDateTime end
    );

    long countByActiveTrueAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime start,
            LocalDateTime end
    );
}