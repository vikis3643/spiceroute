package com.aditya.restaurant_backend.entity;

import java.math.BigDecimal;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
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

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false, length = 1000)
    private String deliveryAddress;

    @Column(name = "delivery_latitude")
    private Double deliveryLatitude;

    @Column(name = "delivery_longitude")
    private Double deliveryLongitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_status",
            length = 20
    )
    private PaymentStatus paymentStatus =
            PaymentStatus.NOT_REQUIRED;

    @Column(
            name = "transaction_id",
            length = 100
    )
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "order_timing",
            nullable = false,
            length = 20
    )
    private OrderTiming orderTiming =
            OrderTiming.NOW;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "meal_slot",
            length = 20
    )
    private MealSlot mealSlot;

    @Column(name = "scheduled_for")
    private LocalDateTime scheduledFor;

    @Column(name = "preparation_start_at")
    private LocalDateTime preparationStartAt;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal subtotal;

    @Column(
            name = "discount_amount",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal discountAmount =
            BigDecimal.ZERO;

    @Column(
            name = "applied_discount_names",
            length = 500
    )
    private String appliedDiscountNames;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal deliveryFee;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal totalAmount;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items =
            new ArrayList<>();

    public CustomerOrder() {
    }

    @PrePersist
    public void beforeSave() {

        if (discountAmount == null) {
            discountAmount =
                    BigDecimal.ZERO;
        }

        if (status == null) {
            status =
                    OrderStatus.PLACED;
        }

        if (paymentMethod == null) {
            paymentMethod =
                    PaymentMethod.CASH_ON_DELIVERY;
        }

        if (paymentStatus == null) {
            paymentStatus =
                    PaymentStatus.NOT_REQUIRED;
        }

        if (orderTiming == null) {
            orderTiming =
                    OrderTiming.NOW;
        }

        if (createdAt == null) {
            createdAt =
                    LocalDateTime.now();
        }
    }

    public void addItem(
            OrderItem item
    ) {
        items.add(item);
        item.setOrder(this);
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

    // ==========================================
    // RESTAURANT
    // ==========================================

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(
            Restaurant restaurant
    ) {
        this.restaurant =
                restaurant;
    }

    // Safe restaurant information exposed
    // in order JSON responses.

    public Long getRestaurantId() {

        return restaurant == null
                ? null
                : restaurant.getId();
    }

    public String getRestaurantName() {

        return restaurant == null
                ? null
                : restaurant.getName();
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone =
                phone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(
            String deliveryAddress
    ) {
        this.deliveryAddress =
                deliveryAddress;
    }

    public Double getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(
            Double deliveryLatitude
    ) {
        this.deliveryLatitude =
                deliveryLatitude;
    }

    public Double getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDeliveryLongitude(
            Double deliveryLongitude
    ) {
        this.deliveryLongitude =
                deliveryLongitude;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(
            OrderStatus status
    ) {
        this.status =
                status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            PaymentMethod paymentMethod
    ) {
        this.paymentMethod =
                paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(
            PaymentStatus paymentStatus
    ) {
        this.paymentStatus =
                paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(
            String transactionId
    ) {
        this.transactionId =
                transactionId;
    }

    public OrderTiming getOrderTiming() {
        return orderTiming;
    }

    public void setOrderTiming(
            OrderTiming orderTiming
    ) {
        this.orderTiming =
                orderTiming;
    }

    public MealSlot getMealSlot() {
        return mealSlot;
    }

    public void setMealSlot(
            MealSlot mealSlot
    ) {
        this.mealSlot =
                mealSlot;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(
            LocalDateTime scheduledFor
    ) {
        this.scheduledFor =
                scheduledFor;
    }

    public LocalDateTime
            getPreparationStartAt() {

        return preparationStartAt;
    }

    public void setPreparationStartAt(
            LocalDateTime preparationStartAt
    ) {
        this.preparationStartAt =
                preparationStartAt;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(
            BigDecimal subtotal
    ) {
        this.subtotal =
                subtotal;
    }

    public BigDecimal
            getDiscountAmount() {

        return discountAmount;
    }

    public void setDiscountAmount(
            BigDecimal discountAmount
    ) {
        this.discountAmount =
                discountAmount;
    }

    public String
            getAppliedDiscountNames() {

        return appliedDiscountNames;
    }

    public void setAppliedDiscountNames(
            String appliedDiscountNames
    ) {
        this.appliedDiscountNames =
                appliedDiscountNames;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(
            BigDecimal deliveryFee
    ) {
        this.deliveryFee =
                deliveryFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount
    ) {
        this.totalAmount =
                totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(
            List<OrderItem> items
    ) {
        this.items =
                items;
    }
}