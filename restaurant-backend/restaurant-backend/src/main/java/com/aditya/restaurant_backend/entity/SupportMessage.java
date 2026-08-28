package com.aditya.restaurant_backend.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "support_messages")
public class SupportMessage {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @JsonBackReference
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "ticket_id",
            nullable = false
    )
    private SupportTicket ticket;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "sender_type",
            nullable = false,
            length = 20
    )
    private SupportSenderType senderType;

    @Column(
            name = "sender_name",
            nullable = false,
            length = 100
    )
    private String senderName;

    @NotBlank(message = "Support message is required")
    @Size(
            min = 2,
            max = 2000,
            message = "Message must contain 2 to 2000 characters"
    )
    @Column(
            nullable = false,
            length = 2000
    )
    private String message;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    public SupportMessage() {
    }

    @PrePersist
    public void beforeSave() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public SupportTicket getTicket() {
        return ticket;
    }

    public void setTicket(
            SupportTicket ticket
    ) {
        this.ticket = ticket;
    }

    public SupportSenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(
            SupportSenderType senderType
    ) {
        this.senderType = senderType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(
            String senderName
    ) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}