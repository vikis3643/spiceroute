package com.aditya.restaurant_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.PlatformSetting;

public interface PlatformSettingRepository
        extends JpaRepository<PlatformSetting, Long> {

    Optional<PlatformSetting> findTopByOrderByIdAsc();
}