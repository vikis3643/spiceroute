package com.aditya.restaurant_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.CreateDeliveryAssignmentRequest;
import com.aditya.restaurant_backend.dto.DeliveryAssignmentResponse;
import com.aditya.restaurant_backend.dto.DeliveryAssignmentStatusRequest;
import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.DeliveryAssignment;
import com.aditya.restaurant_backend.entity.DeliveryAssignmentStatus;
import com.aditya.restaurant_backend.entity.DeliveryPartner;
import com.aditya.restaurant_backend.entity.DeliveryPartnerStatus;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.repository.DeliveryAssignmentRepository;
import com.aditya.restaurant_backend.repository.DeliveryPartnerRepository;
import com.aditya.restaurant_backend.repository.OrderRepository;

@Service
public class SuperAdminDeliveryAssignmentService {

    private final DeliveryAssignmentRepository
            deliveryAssignmentRepository;

    private final DeliveryPartnerRepository
            deliveryPartnerRepository;

    private final OrderRepository
            orderRepository;

    public SuperAdminDeliveryAssignmentService(
            DeliveryAssignmentRepository deliveryAssignmentRepository,
            DeliveryPartnerRepository deliveryPartnerRepository,
            OrderRepository orderRepository
    ) {
        this.deliveryAssignmentRepository =
                deliveryAssignmentRepository;

        this.deliveryPartnerRepository =
                deliveryPartnerRepository;

        this.orderRepository =
                orderRepository;
    }

    // ==========================================
    // LIST ALL ASSIGNMENTS
    // ==========================================

    @Transactional(readOnly = true)
    public List<DeliveryAssignmentResponse>
            getAllAssignments() {

        return deliveryAssignmentRepository
                .findAllByOrderByAssignedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // GET ONE ASSIGNMENT
    // ==========================================

    @Transactional(readOnly = true)
    public DeliveryAssignmentResponse
            getAssignment(
                    Long assignmentId
            ) {

        return toResponse(
                findAssignment(
                        assignmentId
                )
        );
    }

    // ==========================================
    // GET ASSIGNMENT BY ORDER
    // ==========================================

    @Transactional(readOnly = true)
    public DeliveryAssignmentResponse
            getAssignmentByOrder(
                    Long orderId
            ) {

        DeliveryAssignment assignment =
                deliveryAssignmentRepository
                        .findByOrderId(
                                orderId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Delivery assignment not found for order: "
                                                + orderId
                                )
                        );

        return toResponse(
                assignment
        );
    }

    // ==========================================
    // CREATE ASSIGNMENT
    // ==========================================

    @Transactional
    public DeliveryAssignmentResponse
            createAssignment(
                    CreateDeliveryAssignmentRequest request
            ) {

        if (
                deliveryAssignmentRepository
                        .existsByOrderId(
                                request.orderId()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This order already has a delivery assignment"
            );
        }

        CustomerOrder order =
                orderRepository
                        .findById(
                                request.orderId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Order not found with id: "
                                                + request.orderId()
                                )
                        );

        if (
                order.getStatus()
                        == OrderStatus.DELIVERED
                ||
                order.getStatus()
                        == OrderStatus.CANCELLED
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Delivered or cancelled order cannot be assigned"
            );
        }

        DeliveryPartner partner =
                findPartner(
                        request.deliveryPartnerId()
                );

        if (!partner.isActive()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Inactive delivery partner cannot receive assignments"
            );
        }

        if (
                partner.getStatus()
                        != DeliveryPartnerStatus.AVAILABLE
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Delivery partner must be AVAILABLE"
            );
        }

        DeliveryAssignment assignment =
                new DeliveryAssignment();

        assignment.setOrder(
                order
        );

        assignment.setDeliveryPartner(
                partner
        );

        assignment.setStatus(
                DeliveryAssignmentStatus.ASSIGNED
        );

        assignment.setAssignedAt(
                LocalDateTime.now()
        );

        partner.setStatus(
                DeliveryPartnerStatus.BUSY
        );

        deliveryPartnerRepository.save(
                partner
        );

        DeliveryAssignment savedAssignment =
                deliveryAssignmentRepository
                        .save(
                                assignment
                        );

        return toResponse(
                savedAssignment
        );
    }

    // ==========================================
    // UPDATE ASSIGNMENT STATUS
    // ==========================================

    @Transactional
    public DeliveryAssignmentResponse
            updateStatus(
                    Long assignmentId,
                    DeliveryAssignmentStatusRequest request
            ) {

        DeliveryAssignment assignment =
                findAssignment(
                        assignmentId
                );

        DeliveryAssignmentStatus newStatus =
                request.status();

        CustomerOrder order =
                assignment.getOrder();

        DeliveryPartner partner =
                assignment.getDeliveryPartner();

        LocalDateTime now =
                LocalDateTime.now();

        assignment.setStatus(
                newStatus
        );

        switch (newStatus) {

            case ASSIGNED -> {

                assignment.setAssignedAt(
                        now
                );

                partner.setStatus(
                        DeliveryPartnerStatus.BUSY
                );
            }

            case ACCEPTED -> {

                assignment.setAcceptedAt(
                        now
                );

                partner.setStatus(
                        DeliveryPartnerStatus.BUSY
                );
            }

            case PICKED_UP -> {

                assignment.setPickedUpAt(
                        now
                );

                partner.setStatus(
                        DeliveryPartnerStatus.BUSY
                );

                order.setStatus(
                        OrderStatus.OUT_FOR_DELIVERY
                );
            }

            case DELIVERED -> {

                assignment.setDeliveredAt(
                        now
                );

                partner.setStatus(
                        DeliveryPartnerStatus.AVAILABLE
                );

                order.setStatus(
                        OrderStatus.DELIVERED
                );
            }

            case CANCELLED -> {

                assignment.setCancelledAt(
                        now
                );

                partner.setStatus(
                        DeliveryPartnerStatus.AVAILABLE
                );
            }
        }

        orderRepository.save(
                order
        );

        deliveryPartnerRepository.save(
                partner
        );

        DeliveryAssignment savedAssignment =
                deliveryAssignmentRepository
                        .save(
                                assignment
                        );

        return toResponse(
                savedAssignment
        );
    }

    // ==========================================
    // FIND ASSIGNMENT
    // ==========================================

    private DeliveryAssignment findAssignment(
            Long assignmentId
    ) {

        return deliveryAssignmentRepository
                .findById(
                        assignmentId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Delivery assignment not found with id: "
                                        + assignmentId
                        )
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

    private DeliveryAssignmentResponse
            toResponse(
                    DeliveryAssignment assignment
            ) {

        CustomerOrder order =
                assignment.getOrder();

        DeliveryPartner partner =
                assignment.getDeliveryPartner();

        return new DeliveryAssignmentResponse(

                assignment.getId(),

                order.getId(),

                order.getRestaurant().getId(),
                order.getRestaurant().getName(),

                order.getCustomerName(),
                order.getPhone(),
                order.getDeliveryAddress(),

                partner.getId(),
                partner.getFullName(),
                partner.getPhone(),

                assignment.getStatus(),

                assignment.getAssignedAt(),
                assignment.getAcceptedAt(),
                assignment.getPickedUpAt(),
                assignment.getDeliveredAt(),
                assignment.getCancelledAt(),

                assignment.getCreatedAt(),
                assignment.getUpdatedAt()
        );
    }
}