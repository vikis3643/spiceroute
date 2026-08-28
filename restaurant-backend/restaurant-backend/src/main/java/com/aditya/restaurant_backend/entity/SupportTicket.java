package com.aditya.restaurant_backend.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "customer_account_id",
            nullable = false
    )
    private CustomerAccount customerAccount;

    // ==========================================
    // RESTAURANT RELATION
    // ==========================================

    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "restaurant_id",
            nullable = false
    )
    private Restaurant restaurant;

    @Column(
            name = "customer_name",
            nullable = false,
            length = 100
    )
    private String customerName;

    @Column(
            name = "customer_email",
            nullable = false,
            length = 150
    )
    private String customerEmail;

    @NotBlank(
            message = "Ticket subject is required"
    )
    @Size(
            min = 5,
            max = 150,
            message = "Subject must contain 5 to 150 characters"
    )
    @Column(
            nullable = false,
            length = 150
    )
    private String subject;

    @NotNull(
            message = "Ticket category is required"
    )
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private SupportTicketCategory category;

    @NotNull(
            message = "Ticket priority is required"
    )
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private SupportTicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private SupportTicketStatus status =
            SupportTicketStatus.OPEN;

    @Column(name = "order_id")
    private Long orderId;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "ticket",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("createdAt ASC")
    private List<SupportMessage> messages =
            new ArrayList<>();

    public SupportTicket() {
    }

    @PrePersist
    public void beforeSave() {

        LocalDateTime now =
                LocalDateTime.now();

        if (status == null) {
            status =
                    SupportTicketStatus.OPEN;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt =
                LocalDateTime.now();
    }

    public void addMessage(
            SupportMessage message
    ) {
        messages.add(message);

        message.setTicket(this);
    }

    public void markUpdated() {
        updatedAt =
                LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public CustomerAccount
            getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(
            CustomerAccount customerAccount
    ) {
        this.customerAccount =
                customerAccount;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(
            Restaurant restaurant
    ) {
        this.restaurant =
                restaurant;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(
            String customerName
    ) {
        this.customerName =
                customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(
            String customerEmail
    ) {
        this.customerEmail =
                customerEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(
            String subject
    ) {
        this.subject = subject;
    }

    public SupportTicketCategory
            getCategory() {
        return category;
    }

    public void setCategory(
            SupportTicketCategory category
    ) {
        this.category = category;
    }

    public SupportTicketPriority
            getPriority() {
        return priority;
    }

    public void setPriority(
            SupportTicketPriority priority
    ) {
        this.priority = priority;
    }

    public SupportTicketStatus
            getStatus() {
        return status;
    }

    public void setStatus(
            SupportTicketStatus status
    ) {
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(
            Long orderId
    ) {
        this.orderId = orderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<SupportMessage>
            getMessages() {
        return messages;
    }

    public void setMessages(
            List<SupportMessage> messages
    ) {
        this.messages = messages;
    }
}