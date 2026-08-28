package com.aditya.restaurant_backend.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "customer_reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_review_order",
                        columnNames = "order_id"
                )
        }
)
public class CustomerReview {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @JsonIgnore
    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private CustomerOrder order;

    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private CustomerAccount customerAccount;

    @Column(
            name = "food_rating",
            nullable = false
    )
    private int foodRating;

    @Column(
            name = "customer_service_rating",
            nullable = false
    )
    private int customerServiceRating;

    @Column(length = 1000)
    private String comment;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    public CustomerReview() {
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

    public CustomerOrder getOrder() {
        return order;
    }

    public void setOrder(
            CustomerOrder order
    ) {
        this.order = order;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(
            CustomerAccount customerAccount
    ) {
        this.customerAccount = customerAccount;
    }

    public int getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(
            int foodRating
    ) {
        this.foodRating = foodRating;
    }

    public int getCustomerServiceRating() {
        return customerServiceRating;
    }

    public void setCustomerServiceRating(
            int customerServiceRating
    ) {
        this.customerServiceRating =
                customerServiceRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(
            String comment
    ) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}