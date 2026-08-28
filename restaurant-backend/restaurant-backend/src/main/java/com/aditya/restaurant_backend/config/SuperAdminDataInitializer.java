package com.aditya.restaurant_backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.aditya.restaurant_backend.entity.SuperAdmin;
import com.aditya.restaurant_backend.repository.SuperAdminRepository;

@Component
public class SuperAdminDataInitializer
        implements CommandLineRunner {

    private final SuperAdminRepository
            superAdminRepository;

    private final PasswordEncoder
            passwordEncoder;

    public SuperAdminDataInitializer(
            SuperAdminRepository superAdminRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.superAdminRepository =
                superAdminRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    @Override
    public void run(String... args) {

        String superAdminEmail =
                "superadmin@restaurant.local";

        boolean superAdminExists =
                superAdminRepository
                        .existsByEmailIgnoreCase(
                                superAdminEmail
                        );

        if (superAdminExists) {
            return;
        }

        SuperAdmin superAdmin =
                new SuperAdmin();

        superAdmin.setFullName(
                "Platform Super Admin"
        );

        superAdmin.setEmail(
                superAdminEmail
        );

        superAdmin.setPasswordHash(
                passwordEncoder.encode(
                        "SuperAdmin@123"
                )
        );

        superAdmin.setActive(true);

        superAdminRepository.save(
                superAdmin
        );
    }
}