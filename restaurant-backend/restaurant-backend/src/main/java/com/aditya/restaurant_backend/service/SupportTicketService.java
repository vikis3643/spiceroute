package com.aditya.restaurant_backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.CreateSupportTicketRequest;
import com.aditya.restaurant_backend.dto.SupportReplyRequest;
import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.SupportMessage;
import com.aditya.restaurant_backend.entity.SupportSenderType;
import com.aditya.restaurant_backend.entity.SupportTicket;
import com.aditya.restaurant_backend.entity.SupportTicketStatus;
import com.aditya.restaurant_backend.repository.CustomerAccountRepository;
import com.aditya.restaurant_backend.repository.OrderRepository;
import com.aditya.restaurant_backend.repository.RestaurantRepository;
import com.aditya.restaurant_backend.repository.SupportTicketRepository;

@Service
public class SupportTicketService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    SupportTicketService.class
            );

    private final SupportTicketRepository
            supportTicketRepository;

    private final CustomerAccountRepository
            customerAccountRepository;

    private final OrderRepository
            orderRepository;

    private final RestaurantRepository
            restaurantRepository;

    private final EmailService
            emailService;

    public SupportTicketService(
            SupportTicketRepository supportTicketRepository,
            CustomerAccountRepository customerAccountRepository,
            OrderRepository orderRepository,
            RestaurantRepository restaurantRepository,
            EmailService emailService
    ) {
        this.supportTicketRepository =
                supportTicketRepository;

        this.customerAccountRepository =
                customerAccountRepository;

        this.orderRepository =
                orderRepository;

        this.restaurantRepository =
                restaurantRepository;

        this.emailService =
                emailService;
    }

    // ==========================================
    // CUSTOMER - CREATE SUPPORT TICKET
    // ==========================================

    @Transactional
    public SupportTicket createTicket(
            CreateSupportTicketRequest request,
            String customerEmail
    ) {

        CustomerAccount customer =
                findCustomerByEmail(
                        customerEmail
                );

        Restaurant restaurant =
                findRestaurantById(
                        request.restaurantId()
                );

        validateOrderOwnershipAndRestaurant(
                request.orderId(),
                customer,
                restaurant
        );

        SupportTicket ticket =
                new SupportTicket();

        ticket.setCustomerAccount(
                customer
        );

        ticket.setRestaurant(
                restaurant
        );

        ticket.setCustomerName(
                customer.getFullName()
        );

        ticket.setCustomerEmail(
                customer.getEmail()
        );

        ticket.setSubject(
                request.subject().trim()
        );

        ticket.setCategory(
                request.category()
        );

        ticket.setPriority(
                request.priority()
        );

        ticket.setStatus(
                SupportTicketStatus.OPEN
        );

        ticket.setOrderId(
                request.orderId()
        );

        SupportMessage firstMessage =
                createMessage(
                        SupportSenderType.CUSTOMER,
                        customer.getFullName(),
                        request.message()
                );

        ticket.addMessage(
                firstMessage
        );

        SupportTicket savedTicket =
                supportTicketRepository.save(
                        ticket
                );

        try {
            emailService
                    .sendSupportTicketCreatedEmail(
                            savedTicket.getId(),
                            savedTicket.getCustomerName(),
                            savedTicket.getCustomerEmail(),
                            savedTicket.getSubject()
                    );
        } catch (Exception ex) {
            log.error(
                    "Failed to send support ticket creation email for ticket {}",
                    savedTicket.getId(),
                    ex
            );
        }

        return savedTicket;
    }

    // ==========================================
    // CUSTOMER - LIST TICKETS
    // ==========================================

    public List<SupportTicket>
            getCustomerTickets(
                    String customerEmail
            ) {

        CustomerAccount customer =
                findCustomerByEmail(
                        customerEmail
                );

        return supportTicketRepository
                .findByCustomerAccountIdOrderByUpdatedAtDesc(
                        customer.getId()
                );
    }

    // ==========================================
    // CUSTOMER - GET ONE TICKET
    // ==========================================

    public SupportTicket
            getCustomerTicket(
                    Long ticketId,
                    String customerEmail
            ) {

        CustomerAccount customer =
                findCustomerByEmail(
                        customerEmail
                );

        SupportTicket ticket =
                getTicketById(
                        ticketId
                );

        verifyTicketOwnership(
                ticket,
                customer
        );

        return ticket;
    }

    // ==========================================
    // CUSTOMER - REPLY
    // ==========================================

    @Transactional
    public SupportTicket addCustomerReply(
            Long ticketId,
            SupportReplyRequest request,
            String customerEmail
    ) {

        CustomerAccount customer =
                findCustomerByEmail(
                        customerEmail
                );

        SupportTicket ticket =
                getTicketById(
                        ticketId
                );

        verifyTicketOwnership(
                ticket,
                customer
        );

        if (ticket.getStatus()
                == SupportTicketStatus.CLOSED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Closed tickets cannot receive new replies"
            );
        }

        if (ticket.getStatus()
                == SupportTicketStatus.RESOLVED) {

            ticket.setStatus(
                    SupportTicketStatus.OPEN
            );
        }

        SupportMessage reply =
                createMessage(
                        SupportSenderType.CUSTOMER,
                        customer.getFullName(),
                        request.message()
                );

        ticket.addMessage(
                reply
        );

        ticket.markUpdated();

        SupportTicket savedTicket =
                supportTicketRepository.save(
                        ticket
                );

        try {
            emailService
                    .sendSupportTicketCreatedEmail(
                            savedTicket.getId(),
                            savedTicket.getCustomerName(),
                            savedTicket.getCustomerEmail(),
                            savedTicket.getSubject()
                    );
        } catch (Exception ex) {
            log.error(
                    "Failed to send support ticket creation email for ticket {}",
                    savedTicket.getId(),
                    ex
            );
        }

        return savedTicket;
    }

    // ==========================================
    // RESTAURANT ADMIN - LIST OWN TICKETS
    // ==========================================

    public List<SupportTicket>
            getTicketsForRestaurantAdmin(
                    Long restaurantId
            ) {

        return supportTicketRepository
                .findByRestaurantIdOrderByUpdatedAtDesc(
                        restaurantId
                );
    }

    // ==========================================
    // RESTAURANT ADMIN - GET OWN TICKET
    // ==========================================

    public SupportTicket
            getTicketForRestaurantAdmin(
                    Long ticketId,
                    Long restaurantId
            ) {

        return getTicketForRestaurant(
                ticketId,
                restaurantId
        );
    }

    // ==========================================
    // RESTAURANT ADMIN - REPLY
    // ==========================================

    @Transactional
    public SupportTicket
            addRestaurantAdminReply(
                    Long ticketId,
                    SupportReplyRequest request,
                    String adminName,
                    Long restaurantId
            ) {

        SupportTicket ticket =
                getTicketForRestaurant(
                        ticketId,
                        restaurantId
                );

        if (ticket.getStatus()
                == SupportTicketStatus.CLOSED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Closed tickets cannot receive new replies"
            );
        }

        if (ticket.getStatus()
                == SupportTicketStatus.OPEN) {

            ticket.setStatus(
                    SupportTicketStatus.IN_PROGRESS
            );
        }

        String senderName =
                adminName == null
                        || adminName.isBlank()
                        ? "Restaurant Support"
                        : adminName;

        SupportMessage reply =
                createMessage(
                        SupportSenderType.ADMIN,
                        senderName,
                        request.message()
                );

        ticket.addMessage(
                reply
        );

        ticket.markUpdated();

        SupportTicket savedTicket =
                supportTicketRepository.save(
                        ticket
                );

        try {
            emailService
                    .sendSupportAdminReplyEmail(
                            savedTicket.getId(),
                            savedTicket.getCustomerName(),
                            savedTicket.getCustomerEmail(),
                            savedTicket.getSubject(),
                            request.message().trim()
                    );
        } catch (Exception ex) {
            log.error(
                    "Failed to send admin support reply email for ticket {}",
                    savedTicket.getId(),
                    ex
            );
        }

        return savedTicket;
    }

    // ==========================================
    // RESTAURANT ADMIN - UPDATE STATUS
    // ==========================================

    @Transactional
    public SupportTicket
            updateRestaurantTicketStatus(
                    Long ticketId,
                    SupportTicketStatus status,
                    Long restaurantId
            ) {

        SupportTicket ticket =
                getTicketForRestaurant(
                        ticketId,
                        restaurantId
                );

        ticket.setStatus(
                status
        );

        ticket.markUpdated();

        SupportTicket savedTicket =
                supportTicketRepository.save(
                        ticket
                );

        try {
            emailService
                    .sendSupportStatusChangedEmail(
                            savedTicket.getId(),
                            savedTicket.getCustomerName(),
                            savedTicket.getCustomerEmail(),
                            savedTicket.getSubject(),
                            savedTicket
                                    .getStatus()
                                    .name()
                    );
        } catch (Exception ex) {
            log.error(
                    "Failed to send support status email for ticket {}",
                    savedTicket.getId(),
                    ex
            );
        }

        return savedTicket;
    }

    // ==========================================
    // MESSAGE CREATION
    // ==========================================

    private SupportMessage createMessage(
            SupportSenderType senderType,
            String senderName,
            String messageText
    ) {

        SupportMessage message =
                new SupportMessage();

        message.setSenderType(
                senderType
        );

        message.setSenderName(
                senderName
        );

        message.setMessage(
                messageText.trim()
        );

        return message;
    }

    // ==========================================
    // GENERIC TICKET LOOKUP
    // CUSTOMER SIDE
    // ==========================================

    private SupportTicket getTicketById(
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
    // RESTAURANT-SCOPED TICKET LOOKUP
    // ==========================================

    private SupportTicket
            getTicketForRestaurant(
                    Long ticketId,
                    Long restaurantId
            ) {

        return supportTicketRepository
                .findByIdAndRestaurantId(
                        ticketId,
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Support ticket not found for this restaurant"
                        )
                );
    }

    // ==========================================
    // CUSTOMER LOOKUP
    // ==========================================

    private CustomerAccount
            findCustomerByEmail(
                    String customerEmail
            ) {

        return customerAccountRepository
                .findByEmailIgnoreCase(
                        customerEmail
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Customer account not found"
                        )
                );
    }

    // ==========================================
    // RESTAURANT LOOKUP
    // ==========================================

    private Restaurant findRestaurantById(
            Long restaurantId
    ) {

        return restaurantRepository
                .findById(
                        restaurantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Restaurant not found"
                        )
                );
    }

    // ==========================================
    // ORDER + RESTAURANT VALIDATION
    // ==========================================

    private void
            validateOrderOwnershipAndRestaurant(
                    Long orderId,
                    CustomerAccount customer,
                    Restaurant restaurant
            ) {

        if (orderId == null) {
            return;
        }

        CustomerOrder order =
                orderRepository
                        .findById(
                                orderId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Selected order was not found"
                                )
                        );

        if (order.getCustomerAccount()
                == null
                || !order
                        .getCustomerAccount()
                        .getId()
                        .equals(
                                customer.getId()
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot create a ticket for this order"
            );
        }

        if (order.getRestaurant()
                == null
                || !order
                        .getRestaurant()
                        .getId()
                        .equals(
                                restaurant.getId()
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected order does not belong to this restaurant"
            );
        }
    }

    // ==========================================
    // CUSTOMER TICKET OWNERSHIP
    // ==========================================

    private void verifyTicketOwnership(
            SupportTicket ticket,
            CustomerAccount customer
    ) {

        if (ticket.getCustomerAccount()
                == null
                || !ticket
                        .getCustomerAccount()
                        .getId()
                        .equals(
                                customer.getId()
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access this support ticket"
            );
        }
    }
}