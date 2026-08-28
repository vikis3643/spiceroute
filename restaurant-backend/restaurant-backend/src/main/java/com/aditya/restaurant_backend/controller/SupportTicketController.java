package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.CreateSupportTicketRequest;
import com.aditya.restaurant_backend.dto.SupportReplyRequest;
import com.aditya.restaurant_backend.entity.SupportTicket;
import com.aditya.restaurant_backend.entity.SupportTicketStatus;
import com.aditya.restaurant_backend.service.SupportTicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/support")
public class SupportTicketController {

    private final SupportTicketService
            supportTicketService;

    public SupportTicketController(
            SupportTicketService supportTicketService
    ) {
        this.supportTicketService =
                supportTicketService;
    }

    // ==========================================
    // CUSTOMER ENDPOINTS
    // ==========================================

    @PostMapping("/tickets")
    public ResponseEntity<SupportTicket>
            createTicket(
                    @Valid
                    @RequestBody
                    CreateSupportTicketRequest request,
                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        SupportTicket savedTicket =
                supportTicketService
                        .createTicket(
                                request,
                                jwt.getSubject()
                        );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        savedTicket
                );
    }

    @GetMapping(
            "/tickets/my-tickets"
    )
    public List<SupportTicket>
            getMyTickets(
                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        return supportTicketService
                .getCustomerTickets(
                        jwt.getSubject()
                );
    }

    @GetMapping(
            "/tickets/my-tickets/{ticketId}"
    )
    public SupportTicket getMyTicket(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return supportTicketService
                .getCustomerTicket(
                        ticketId,
                        jwt.getSubject()
                );
    }

    @PostMapping(
            "/tickets/my-tickets/{ticketId}/replies"
    )
    public SupportTicket addCustomerReply(
            @PathVariable Long ticketId,
            @Valid
            @RequestBody
            SupportReplyRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return supportTicketService
                .addCustomerReply(
                        ticketId,
                        request,
                        jwt.getSubject()
                );
    }

    // ==========================================
    // RESTAURANT ADMIN ENDPOINTS
    // ==========================================

    @GetMapping(
            "/restaurant-admin/tickets"
    )
    public List<SupportTicket>
            getRestaurantAdminTickets(
                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim(
                        "restaurantId"
                );

        return supportTicketService
                .getTicketsForRestaurantAdmin(
                        restaurantId
                );
    }

    @GetMapping(
            "/restaurant-admin/tickets/{ticketId}"
    )
    public SupportTicket
            getRestaurantAdminTicket(
                    @PathVariable
                    Long ticketId,
                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim(
                        "restaurantId"
                );

        return supportTicketService
                .getTicketForRestaurantAdmin(
                        ticketId,
                        restaurantId
                );
    }

    @PostMapping(
            "/restaurant-admin/tickets/{ticketId}/replies"
    )
    public SupportTicket
            addRestaurantAdminReply(
                    @PathVariable
                    Long ticketId,
                    @Valid
                    @RequestBody
                    SupportReplyRequest request,
                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim(
                        "restaurantId"
                );

        String adminName =
                jwt.getSubject() == null
                        ? "Restaurant Support"
                        : jwt.getSubject();

        return supportTicketService
                .addRestaurantAdminReply(
                        ticketId,
                        request,
                        adminName,
                        restaurantId
                );
    }

    @PatchMapping(
            "/restaurant-admin/tickets/{ticketId}/status"
    )
    public SupportTicket
            updateRestaurantAdminTicketStatus(
                    @PathVariable
                    Long ticketId,
                    @RequestParam
                    SupportTicketStatus status,
                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim(
                        "restaurantId"
                );

        return supportTicketService
                .updateRestaurantTicketStatus(
                        ticketId,
                        status,
                        restaurantId
                );
    }
}