package com.aditya.restaurant_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.SupportTicket;
import com.aditya.restaurant_backend.entity.SupportTicketPriority;
import com.aditya.restaurant_backend.entity.SupportTicketStatus;

public interface SupportTicketRepository
        extends JpaRepository<SupportTicket, Long> {

    // ==========================================
    // CUSTOMER METHODS
    // ==========================================

    @EntityGraph(attributePaths = "messages")
    List<SupportTicket>
            findByCustomerAccountIdOrderByUpdatedAtDesc(
                    Long customerAccountId
            );

    // ==========================================
    // PLATFORM-WIDE LIST
    // ==========================================

    @EntityGraph(attributePaths = "messages")
    List<SupportTicket>
            findAllByOrderByUpdatedAtDesc();

    // ==========================================
    // RESTAURANT ADMIN ISOLATION
    // ==========================================

    @EntityGraph(attributePaths = "messages")
    List<SupportTicket>
            findByRestaurantIdOrderByUpdatedAtDesc(
                    Long restaurantId
            );

    @EntityGraph(attributePaths = "messages")
    Optional<SupportTicket>
            findByIdAndRestaurantId(
                    Long ticketId,
                    Long restaurantId
            );

    // ==========================================
    // SUPER ADMIN FILTERS
    // ==========================================

    @EntityGraph(attributePaths = "messages")
    List<SupportTicket>
            findByStatusOrderByUpdatedAtDesc(
                    SupportTicketStatus status
            );

    @EntityGraph(attributePaths = "messages")
    List<SupportTicket>
            findByPriorityOrderByUpdatedAtDesc(
                    SupportTicketPriority priority
            );

    @EntityGraph(attributePaths = "messages")
    List<SupportTicket>
            findByRestaurantIdAndStatusOrderByUpdatedAtDesc(
                    Long restaurantId,
                    SupportTicketStatus status
            );

    @EntityGraph(attributePaths = "messages")
    List<SupportTicket>
            findByRestaurantIdAndPriorityOrderByUpdatedAtDesc(
                    Long restaurantId,
                    SupportTicketPriority priority
            );

    @EntityGraph(attributePaths = "messages")
    List<SupportTicket>
            findByStatusAndPriorityOrderByUpdatedAtDesc(
                    SupportTicketStatus status,
                    SupportTicketPriority priority
            );

    @EntityGraph(attributePaths = "messages")
    List<SupportTicket>
            findByRestaurantIdAndStatusAndPriorityOrderByUpdatedAtDesc(
                    Long restaurantId,
                    SupportTicketStatus status,
                    SupportTicketPriority priority
            );

    // ==========================================
    // SINGLE TICKET
    // ==========================================

    @Override
    @EntityGraph(attributePaths = "messages")
    Optional<SupportTicket>
            findById(
                    Long id
            );
}