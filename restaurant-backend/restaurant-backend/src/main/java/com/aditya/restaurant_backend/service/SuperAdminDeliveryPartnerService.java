package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.CreateDeliveryPartnerRequest;
import com.aditya.restaurant_backend.dto.DeliveryPartnerActiveRequest;
import com.aditya.restaurant_backend.dto.DeliveryPartnerResponse;
import com.aditya.restaurant_backend.dto.DeliveryPartnerStatusRequest;
import com.aditya.restaurant_backend.dto.UpdateDeliveryPartnerRequest;
import com.aditya.restaurant_backend.entity.DeliveryAssignmentStatus;
import com.aditya.restaurant_backend.entity.DeliveryPartner;
import com.aditya.restaurant_backend.entity.DeliveryPartnerStatus;
import com.aditya.restaurant_backend.repository.DeliveryAssignmentRepository;
import com.aditya.restaurant_backend.repository.DeliveryPartnerRepository;

@Service
public class SuperAdminDeliveryPartnerService {

    private final DeliveryPartnerRepository
            deliveryPartnerRepository;

    private final DeliveryAssignmentRepository
            deliveryAssignmentRepository;

    public SuperAdminDeliveryPartnerService(
            DeliveryPartnerRepository deliveryPartnerRepository,
            DeliveryAssignmentRepository deliveryAssignmentRepository
    ) {

        this.deliveryPartnerRepository =
                deliveryPartnerRepository;

        this.deliveryAssignmentRepository =
                deliveryAssignmentRepository;
    }

    // ==========================================
    // GET ALL DELIVERY PARTNERS
    // ==========================================

    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponse>
            getAllPartners() {

        return deliveryPartnerRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // FILTER BY STATUS
    // ==========================================

    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponse>
            getPartnersByStatus(
                    DeliveryPartnerStatus status
            ) {

        return deliveryPartnerRepository
                .findByStatusOrderByFullNameAsc(
                        status
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // GET AVAILABLE PARTNERS
    // ==========================================

    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponse>
            getAvailablePartners() {

        return deliveryPartnerRepository
                .findByActiveTrueAndStatusOrderByFullNameAsc(
                        DeliveryPartnerStatus.AVAILABLE
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // GET ONE DELIVERY PARTNER
    // ==========================================

    @Transactional(readOnly = true)
    public DeliveryPartnerResponse
            getPartner(
                    Long partnerId
            ) {

        return toResponse(
                findPartner(
                        partnerId
                )
        );
    }

    // ==========================================
    // CREATE DELIVERY PARTNER
    // ==========================================

    @Transactional
    public DeliveryPartnerResponse
            createPartner(
                    CreateDeliveryPartnerRequest request
            ) {

        String email =
                normalizeEmail(
                        request.email()
                );

        String phone =
                request.phone()
                        .trim();

        if (
                deliveryPartnerRepository
                        .existsByEmailIgnoreCase(
                                email
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A delivery partner with this email already exists"
            );
        }

        if (
                deliveryPartnerRepository
                        .existsByPhone(
                                phone
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A delivery partner with this phone already exists"
            );
        }

        DeliveryPartner partner =
                new DeliveryPartner();

        partner.setFullName(
                request.fullName()
                        .trim()
        );

        partner.setEmail(
                email
        );

        partner.setPhone(
                phone
        );

        partner.setVehicleNumber(
                trimToNull(
                        request.vehicleNumber()
                )
        );

        partner.setVehicleType(
                trimToNull(
                        request.vehicleType()
                )
        );

        partner.setStatus(
                DeliveryPartnerStatus.OFFLINE
        );

        partner.setActive(
                true
        );

        DeliveryPartner savedPartner =
                deliveryPartnerRepository.save(
                        partner
                );

        return toResponse(
                savedPartner
        );
    }

    // ==========================================
    // UPDATE DELIVERY PARTNER
    // ==========================================

    @Transactional
    public DeliveryPartnerResponse
            updatePartner(
                    Long partnerId,
                    UpdateDeliveryPartnerRequest request
            ) {

        DeliveryPartner partner =
                findPartner(
                        partnerId
                );

        String email =
                normalizeEmail(
                        request.email()
                );

        String phone =
                request.phone()
                        .trim();

        deliveryPartnerRepository
                .findByEmailIgnoreCase(
                        email
                )
                .ifPresent(existing -> {

                    if (
                            !existing.getId()
                                    .equals(
                                            partnerId
                                    )
                    ) {

                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "A delivery partner with this email already exists"
                        );
                    }
                });

        deliveryPartnerRepository
                .findByPhone(
                        phone
                )
                .ifPresent(existing -> {

                    if (
                            !existing.getId()
                                    .equals(
                                            partnerId
                                    )
                    ) {

                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "A delivery partner with this phone already exists"
                        );
                    }
                });

        partner.setFullName(
                request.fullName()
                        .trim()
        );

        partner.setEmail(
                email
        );

        partner.setPhone(
                phone
        );

        partner.setVehicleNumber(
                trimToNull(
                        request.vehicleNumber()
                )
        );

        partner.setVehicleType(
                trimToNull(
                        request.vehicleType()
                )
        );

        DeliveryPartner savedPartner =
                deliveryPartnerRepository.save(
                        partner
                );

        return toResponse(
                savedPartner
        );
    }

    // ==========================================
    // ACTIVATE / DEACTIVATE
    // ==========================================

    @Transactional
    public DeliveryPartnerResponse
            updateActiveStatus(
                    Long partnerId,
                    DeliveryPartnerActiveRequest request
            ) {

        DeliveryPartner partner =
                findPartner(
                        partnerId
                );

        partner.setActive(
                request.active()
        );

        /*
         * Inactive partner must not remain
         * AVAILABLE or BUSY.
         */
        if (!request.active()) {

            partner.setStatus(
                    DeliveryPartnerStatus.OFFLINE
            );
        }

        DeliveryPartner savedPartner =
                deliveryPartnerRepository.save(
                        partner
                );

        return toResponse(
                savedPartner
        );
    }

    // ==========================================
    // UPDATE WORK STATUS
    // ==========================================

    @Transactional
    public DeliveryPartnerResponse
            updateStatus(
                    Long partnerId,
                    DeliveryPartnerStatusRequest request
            ) {

        DeliveryPartner partner =
                findPartner(
                        partnerId
                );

        if (!partner.isActive()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Inactive delivery partner cannot change work status"
            );
        }

        partner.setStatus(
                request.status()
        );

        DeliveryPartner savedPartner =
                deliveryPartnerRepository.save(
                        partner
                );

        return toResponse(
                savedPartner
        );
    }

    // ==========================================
    // FIND PARTNER
    // ==========================================

    private DeliveryPartner findPartner(
            Long partnerId
    ) {

        return deliveryPartnerRepository
                .findById(
                        partnerId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Delivery partner not found with id: "
                                        + partnerId
                        )
                );
    }

    // ==========================================
    // ENTITY -> RESPONSE
    // ==========================================

    private DeliveryPartnerResponse
            toResponse(
                    DeliveryPartner partner
            ) {

        long totalAssignments =
                deliveryAssignmentRepository
                        .countByDeliveryPartnerId(
                                partner.getId()
                        );

        long deliveredAssignments =
                deliveryAssignmentRepository
                        .countByDeliveryPartnerIdAndStatus(
                                partner.getId(),
                                DeliveryAssignmentStatus.DELIVERED
                        );

        return new DeliveryPartnerResponse(

                partner.getId(),
                partner.getFullName(),
                partner.getEmail(),
                partner.getPhone(),

                partner.getVehicleNumber(),
                partner.getVehicleType(),

                partner.getStatus(),
                partner.isActive(),

                totalAssignments,
                deliveredAssignments,

                partner.getCreatedAt(),
                partner.getUpdatedAt()
        );
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private String normalizeEmail(
            String email
    ) {

        return email
                .trim()
                .toLowerCase();
    }

    private String trimToNull(
            String value
    ) {

        if (
                value == null
                || value.isBlank()
        ) {

            return null;
        }

        return value.trim();
    }
}