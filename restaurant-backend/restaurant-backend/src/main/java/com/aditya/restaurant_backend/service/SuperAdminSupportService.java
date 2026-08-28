package com.aditya.restaurant_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.SuperAdminSupportMessageResponse;
import com.aditya.restaurant_backend.dto.SuperAdminSupportStatusRequest;
import com.aditya.restaurant_backend.dto.SuperAdminSupportTicketResponse;
import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.SupportMessage;
import com.aditya.restaurant_backend.entity.SupportTicket;
import com.aditya.restaurant_backend.entity.SupportTicketPriority;
import com.aditya.restaurant_backend.entity.SupportTicketStatus;
import com.aditya.restaurant_backend.repository.SupportTicketRepository;

@Service
public class SuperAdminSupportService {

    private final SupportTicketRepository
            supportTicketRepository;

    public SuperAdminSupportService(
            SupportTicketRepository supportTicketRepository
    ) {
        this.supportTicketRepository =
                supportTicketRepository;
    }

    // ==========================================
    // LIST / FILTER SUPPORT TICKETS
    // ==========================================

    @Transactional(readOnly = true)
    public List<SuperAdminSupportTicketResponse>
            getTickets(
                    Long restaurantId,
                    SupportTicketStatus status,
                    SupportTicketPriority priority
            ) {

        List<SupportTicket> tickets;

        if (
                restaurantId != null
                && status != null
                && priority != null
        ) {

            tickets =
                    supportTicketRepository
                            .findByRestaurantIdAndStatusAndPriorityOrderByUpdatedAtDesc(
                                    restaurantId,
                                    status,
                                    priority
                            );

        } else if (
                restaurantId != null
                && status != null
        ) {

            tickets =
                    supportTicketRepository
                            .findByRestaurantIdAndStatusOrderByUpdatedAtDesc(
                                    restaurantId,
                                    status
                            );

        } else if (
                restaurantId != null
                && priority != null
        ) {

            tickets =
                    supportTicketRepository
                            .findByRestaurantIdAndPriorityOrderByUpdatedAtDesc(
                                    restaurantId,
                                    priority
                            );

        } else if (
                status != null
                && priority != null
        ) {

            tickets =
                    supportTicketRepository
                            .findByStatusAndPriorityOrderByUpdatedAtDesc(
                                    status,
                                    priority
                            );

        } else if (
                restaurantId != null
        ) {

            tickets =
                    supportTicketRepository
                            .findByRestaurantIdOrderByUpdatedAtDesc(
                                    restaurantId
                            );

        } else if (
                status != null
        ) {

            tickets =
                    supportTicketRepository
                            .findByStatusOrderByUpdatedAtDesc(
                                    status
                            );

        } else if (
                priority != null
        ) {

            tickets =
                    supportTicketRepository
                            .findByPriorityOrderByUpdatedAtDesc(
                                    priority
                            );

        } else {

            tickets =
                    supportTicketRepository
                            .findAllByOrderByUpdatedAtDesc();
        }

        return tickets
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // GET SINGLE TICKET
    // ==========================================

    @Transactional(readOnly = true)
    public SuperAdminSupportTicketResponse
            getTicket(
                    Long ticketId
            ) {

        SupportTicket ticket =
                findTicket(
                        ticketId
                );

        return toResponse(
                ticket
        );
    }

    // ==========================================
    // UPDATE TICKET STATUS
    // ==========================================

    @Transactional
    public SuperAdminSupportTicketResponse
            updateStatus(
                    Long ticketId,
                    SuperAdminSupportStatusRequest request
            ) {

        SupportTicket ticket =
                findTicket(
                        ticketId
                );

        ticket.setStatus(
                request.status()
        );

        ticket.markUpdated();

        SupportTicket savedTicket =
                supportTicketRepository
                        .save(
                                ticket
                        );

        return toResponse(
                savedTicket
        );
    }

    // ==========================================
    // FIND TICKET
    // ==========================================

    private SupportTicket findTicket(
            Long ticketId
    ) {

        return supportTicketRepository
                .findById(
                        ticketId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Support ticket not found with id: "
                                        + ticketId
                        )
                );
    }

    // ==========================================
    // ENTITY -> RESPONSE DTO
    // ==========================================

    private SuperAdminSupportTicketResponse
            toResponse(
                    SupportTicket ticket
            ) {

        Restaurant restaurant =
                ticket.getRestaurant();

        CustomerAccount customer =
                ticket.getCustomerAccount();

        List<SuperAdminSupportMessageResponse>
                messages =
                ticket.getMessages()
                        .stream()
                        .map(
                                this::toMessageResponse
                        )
                        .toList();

        return new SuperAdminSupportTicketResponse(

                ticket.getId(),

                restaurant.getId(),
                restaurant.getName(),

                customer.getId(),
                ticket.getCustomerName(),
                ticket.getCustomerEmail(),

                ticket.getSubject(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getStatus(),

                ticket.getOrderId(),

                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),

                messages
        );
    }

    private SuperAdminSupportMessageResponse
            toMessageResponse(
                    SupportMessage message
            ) {

        return new SuperAdminSupportMessageResponse(
                message.getId(),
                message.getSenderType().name(),
                message.getSenderName(),
                message.getMessage(),
                message.getCreatedAt()
        );
    }
}