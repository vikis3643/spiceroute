package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.SuperAdminSupportStatusRequest;
import com.aditya.restaurant_backend.dto.SuperAdminSupportTicketResponse;
import com.aditya.restaurant_backend.entity.SupportTicketPriority;
import com.aditya.restaurant_backend.entity.SupportTicketStatus;
import com.aditya.restaurant_backend.service.SuperAdminSupportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super-admin/support")
public class SuperAdminSupportController {

    private final SuperAdminSupportService
            superAdminSupportService;

    public SuperAdminSupportController(
            SuperAdminSupportService superAdminSupportService
    ) {
        this.superAdminSupportService =
                superAdminSupportService;
    }

    // ==========================================
    // LIST / FILTER SUPPORT TICKETS
    // ==========================================

    @GetMapping("/tickets")
    public ResponseEntity<
            List<SuperAdminSupportTicketResponse>
            > getTickets(

                    @RequestParam(
                            required = false
                    )
                    Long restaurantId,

                    @RequestParam(
                            required = false
                    )
                    SupportTicketStatus status,

                    @RequestParam(
                            required = false
                    )
                    SupportTicketPriority priority
            ) {

        return ResponseEntity.ok(
                superAdminSupportService
                        .getTickets(
                                restaurantId,
                                status,
                                priority
                        )
        );
    }

    // ==========================================
    // GET SINGLE TICKET
    // ==========================================

    @GetMapping(
            "/tickets/{ticketId}"
    )
    public ResponseEntity<
            SuperAdminSupportTicketResponse
            > getTicket(
                    @PathVariable
                    Long ticketId
            ) {

        return ResponseEntity.ok(
                superAdminSupportService
                        .getTicket(
                                ticketId
                        )
        );
    }

    // ==========================================
    // UPDATE TICKET STATUS
    // ==========================================

    @PatchMapping(
            "/tickets/{ticketId}/status"
    )
    public ResponseEntity<
            SuperAdminSupportTicketResponse
            > updateStatus(
                    @PathVariable
                    Long ticketId,

                    @Valid
                    @RequestBody
                    SuperAdminSupportStatusRequest request
            ) {

        return ResponseEntity.ok(
                superAdminSupportService
                        .updateStatus(
                                ticketId,
                                request
                        )
        );
    }
}