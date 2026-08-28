package com.aditya.restaurant_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.SuperAdmin;

public interface SuperAdminRepository
        extends JpaRepository<SuperAdmin, Long> {

    Optional<SuperAdmin>
            findByEmailIgnoreCase(
                    String email
            );

    boolean existsByEmailIgnoreCase(
            String email
    );
}