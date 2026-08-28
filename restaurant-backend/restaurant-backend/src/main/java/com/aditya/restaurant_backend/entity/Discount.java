package com.aditya.restaurant_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @NotBlank(message = "Discount name is required")
    @Size(
            max = 100,
            message = "Discount name cannot exceed 100 characters"
    )
    @Column(nullable = false, length = 100)
    private String name;

    @Size(
            max = 500,
            message = "Description cannot exceed 500 characters"
    )
    @Column(length = 500)
    private String description;

    @NotNull(message = "Discount type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DiscountType discountType;

    @NotNull(message = "Discount scope is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DiscountScope discountScope;

    @NotNull(message = "Discount value is required")
    @DecimalMin(
            value = "0.01",
            message = "Discount value must be greater than zero"
    )
    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal discountValue;

    @DecimalMin(
            value = "0.00",
            message = "Minimum order amount cannot be negative"
    )
    @Column(
            name = "minimum_order_amount",
            precision = 10,
            scale = 2
    )
    private BigDecimal minimumOrderAmount;

    @DecimalMin(
            value = "0.01",
            message = "Maximum discount must be greater than zero"
    )
    @Column(
            name = "maximum_discount_amount",
            precision = 10,
            scale = 2
    )
    private BigDecimal maximumDiscountAmount;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    // ==========================================
    // RESTAURANT OWNERSHIP
    // Temporary nullable during migration
    // ==========================================

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
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    public Discount() {
    }

    @PrePersist
    public void beforeSave() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (minimumOrderAmount == null) {
            minimumOrderAmount =
                    BigDecimal.ZERO;
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(
            DiscountType discountType
    ) {
        this.discountType = discountType;
    }

    public DiscountScope getDiscountScope() {
        return discountScope;
    }

    public void setDiscountScope(
            DiscountScope discountScope
    ) {
        this.discountScope = discountScope;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(
            BigDecimal discountValue
    ) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(
            BigDecimal minimumOrderAmount
    ) {
        this.minimumOrderAmount =
                minimumOrderAmount;
    }

    public BigDecimal getMaximumDiscountAmount() {
        return maximumDiscountAmount;
    }

    public void setMaximumDiscountAmount(
            BigDecimal maximumDiscountAmount
    ) {
        this.maximumDiscountAmount =
                maximumDiscountAmount;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(
            LocalDateTime startsAt
    ) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(
            LocalDateTime endsAt
    ) {
        this.endsAt = endsAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(
            boolean active
    ) {
        this.active = active;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(
            MenuItem menuItem
    ) {
        this.menuItem = menuItem;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(
            Category category
    ) {
        this.category = category;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(
            Restaurant restaurant
    ) {
        this.restaurant = restaurant;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}