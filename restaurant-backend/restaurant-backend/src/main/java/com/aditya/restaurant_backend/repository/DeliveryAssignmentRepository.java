package com.aditya.restaurant_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.DeliveryAssignment;
import com.aditya.restaurant_backend.entity.DeliveryAssignmentStatus;

public interface DeliveryAssignmentRepository
        extends JpaRepository<DeliveryAssignment, Long> {

    Optional<DeliveryAssignment>
            findByOrderId(
                    Long orderId
            );

    boolean existsByOrderId(
            Long orderId
    );

    List<DeliveryAssignment>
            findByDeliveryPartnerIdOrderByAssignedAtDesc(
                    Long deliveryPartnerId
            );

    List<DeliveryAssignment>
            findByStatusOrderByAssignedAtDesc(
                    DeliveryAssignmentStatus status
            );

    List<DeliveryAssignment>
            findAllByOrderByAssignedAtDesc();

    long countByDeliveryPartnerId(
            Long deliveryPartnerId
    );

    long countByDeliveryPartnerIdAndStatus(
            Long deliveryPartnerId,
            DeliveryAssignmentStatus status
    );
}