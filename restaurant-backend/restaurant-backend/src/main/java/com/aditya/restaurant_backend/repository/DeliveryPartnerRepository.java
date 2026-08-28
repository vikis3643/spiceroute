package com.aditya.restaurant_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.DeliveryPartner;
import com.aditya.restaurant_backend.entity.DeliveryPartnerStatus;

public interface DeliveryPartnerRepository
        extends JpaRepository<DeliveryPartner, Long> {

    Optional<DeliveryPartner>
            findByEmailIgnoreCase(
                    String email
            );

    Optional<DeliveryPartner>
            findByPhone(
                    String phone
            );

    boolean existsByEmailIgnoreCase(
            String email
    );

    boolean existsByPhone(
            String phone
    );

    List<DeliveryPartner>
            findByActiveTrueOrderByFullNameAsc();

    List<DeliveryPartner>
            findByStatusOrderByFullNameAsc(
                    DeliveryPartnerStatus status
            );

    List<DeliveryPartner>
            findByActiveTrueAndStatusOrderByFullNameAsc(
                    DeliveryPartnerStatus status
            );

    long countByActiveTrue();

    long countByActiveFalse();

    long countByStatus(
            DeliveryPartnerStatus status
    );
}