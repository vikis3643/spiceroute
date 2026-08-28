package com.aditya.restaurant_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.entity.PasswordResetToken;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(
            String tokenHash
    );

    void deleteByCustomer(
            CustomerAccount customer
    );
}