package com.aditya.restaurant_backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aditya.restaurant_backend.dto.ResetPasswordRequest;
import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.entity.PasswordResetToken;
import com.aditya.restaurant_backend.repository.CustomerAccountRepository;
import com.aditya.restaurant_backend.repository.PasswordResetTokenRepository;

@Service
public class PasswordResetService {

    private final CustomerAccountRepository
            customerAccountRepository;

    private final PasswordResetTokenRepository
            passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final long expirationMinutes;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public PasswordResetService(
            CustomerAccountRepository
                    customerAccountRepository,
            PasswordResetTokenRepository
                    passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            @Value(
                    "${app.password-reset.expiration-minutes}"
            )
            long expirationMinutes
    ) {
        this.customerAccountRepository =
                customerAccountRepository;

        this.passwordResetTokenRepository =
                passwordResetTokenRepository;

        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.expirationMinutes = expirationMinutes;
    }

    @Transactional
    public void requestPasswordReset(String email) {
        String normalizedEmail = email
                .trim()
                .toLowerCase(Locale.ROOT);

        CustomerAccount customer =
                customerAccountRepository
                        .findByEmailIgnoreCase(
                                normalizedEmail
                        )
                        .orElse(null);

        // Always return normally when an account is absent.
        // This prevents attackers from discovering emails.
        if (customer == null) {
            return;
        }

        passwordResetTokenRepository
                .deleteByCustomer(customer);

        String rawToken = createSecureToken();

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setCustomer(customer);
        resetToken.setTokenHash(
                hashToken(rawToken)
        );

        resetToken.setExpiresAt(
                LocalDateTime.now().plusMinutes(
                        expirationMinutes
                )
        );

        resetToken.setUsed(false);

        passwordResetTokenRepository.save(
                resetToken
        );

        emailService.sendPasswordResetEmail(
                customer.getEmail(),
                customer.getFullName(),
                rawToken
        );
    }

    @Transactional
    public void resetPassword(
            ResetPasswordRequest request
    ) {
        if (!request.getNewPassword().equals(
                request.getConfirmPassword()
        )) {
            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        String tokenHash = hashToken(
                request.getToken()
        );

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid or expired reset link"
                                )
                        );

        if (resetToken.isUsed()
                || resetToken.isExpired()) {
            throw new IllegalArgumentException(
                    "Invalid or expired reset link"
            );
        }

        CustomerAccount customer =
                resetToken.getCustomer();

        customer.setPasswordHash(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        customer.setProvider("LOCAL");
        customerAccountRepository.save(customer);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(
                resetToken
        );
    }

    private String createSecureToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash = digest.digest(
                    token.getBytes(
                            StandardCharsets.UTF_8
                    )
            );

            return HexFormat
                    .of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Unable to secure reset token",
                    exception
            );
        }
    }
}