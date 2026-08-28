package com.aditya.restaurant_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.DiscountCalculationResult;
import com.aditya.restaurant_backend.dto.DiscountQuoteRequest;
import com.aditya.restaurant_backend.dto.DiscountQuoteResponse;
import com.aditya.restaurant_backend.dto.OrderItemRequest;
import com.aditya.restaurant_backend.dto.PlaceOrderRequest;
import com.aditya.restaurant_backend.dto.PlatformSettingResponse;
import com.aditya.restaurant_backend.entity.CustomerAccount;
import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.MenuItem;
import com.aditya.restaurant_backend.entity.OrderItem;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.entity.OrderTiming;
import com.aditya.restaurant_backend.entity.PaymentMethod;
import com.aditya.restaurant_backend.entity.PaymentStatus;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;
import com.aditya.restaurant_backend.repository.CustomerAccountRepository;
import com.aditya.restaurant_backend.repository.MenuItemRepository;
import com.aditya.restaurant_backend.repository.OrderRepository;

@Service
public class OrderService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    OrderService.class
            );

    /*
     * Free delivery threshold remains part of
     * existing order business logic.
     *
     * Delivery fee itself now comes from
     * Platform Settings.
     */
    private static final BigDecimal
            FREE_DELIVERY_MINIMUM =
            new BigDecimal("500.00");

    private final OrderRepository
            orderRepository;

    private final MenuItemRepository
            menuItemRepository;

    private final CustomerAccountRepository
            customerAccountRepository;

    private final EmailService
            emailService;

    private final DiscountCalculationService
            discountCalculationService;

    private final PlatformSettingService
            platformSettingService;

    public OrderService(
            OrderRepository orderRepository,
            MenuItemRepository menuItemRepository,
            CustomerAccountRepository customerAccountRepository,
            EmailService emailService,
            DiscountCalculationService discountCalculationService,
            PlatformSettingService platformSettingService
    ) {

        this.orderRepository =
                orderRepository;

        this.menuItemRepository =
                menuItemRepository;

        this.customerAccountRepository =
                customerAccountRepository;

        this.emailService =
                emailService;

        this.discountCalculationService =
                discountCalculationService;

        this.platformSettingService =
                platformSettingService;
    }

    // ==========================================
    // PLACE ORDER
    // ==========================================

    @Transactional
    public CustomerOrder placeOrder(
            PlaceOrderRequest request,
            String customerEmail
    ) {

        PlatformSettingResponse settings =
                getOperationalSettings();

        ensureOrderingAvailable(
                settings
        );

        CustomerAccount customer =
                findCustomerByEmail(
                        customerEmail
                );

        CustomerOrder order =
                new CustomerOrder();

        order.setCustomerAccount(
                customer
        );

        order.setCustomerName(
                request.customerName()
                        .trim()
        );

        order.setPhone(
                request.phone()
                        .trim()
        );

        order.setDeliveryAddress(
                request.deliveryAddress()
                        .trim()
        );

        order.setDeliveryLatitude(
                request.deliveryLatitude()
        );

        order.setDeliveryLongitude(
                request.deliveryLongitude()
        );

        order.setStatus(
                OrderStatus.PLACED
        );

        configureOrderSchedule(
                order,
                request
        );

        PaymentMethod selectedPaymentMethod =
                request.paymentMethod() == null
                        ? PaymentMethod.CASH_ON_DELIVERY
                        : request.paymentMethod();

        order.setPaymentMethod(
                selectedPaymentMethod
        );

        configureDemoPayment(
                order,
                selectedPaymentMethod
        );

        BigDecimal subtotal =
                BigDecimal.ZERO;

        Restaurant orderRestaurant =
                null;

        for (
                OrderItemRequest requestedItem
                : request.items()
        ) {

            MenuItem menuItem =
                    menuItemRepository
                            .findById(
                                    requestedItem.menuItemId()
                            )
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "Menu item not found with id: "
                                                    + requestedItem
                                                    .menuItemId()
                                    )
                            );

            if (!menuItem.isAvailable()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Menu item is currently unavailable: "
                                + menuItem.getName()
                );
            }

            if (
                    menuItem.getRestaurant()
                            == null
            ) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Menu item is not assigned to a restaurant: "
                                + menuItem.getName()
                );
            }

            Restaurant menuItemRestaurant =
                    menuItem.getRestaurant();

            if (
                    menuItemRestaurant.getApprovalStatus()
                            != RestaurantApprovalStatus.APPROVED
            ) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Restaurant is not approved for ordering"
                );
            }

            if (!menuItemRestaurant.isActive()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Restaurant is currently unavailable"
                );
            }

            if (orderRestaurant == null) {

                orderRestaurant =
                        menuItemRestaurant;

            } else if (
                    !orderRestaurant
                            .getId()
                            .equals(
                                    menuItem
                                            .getRestaurant()
                                            .getId()
                            )
            ) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "All items in an order must belong to the same restaurant"
                );
            }

            BigDecimal lineTotal =
                    menuItem.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            requestedItem
                                                    .quantity()
                                    )
                            );

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setMenuItemId(
                    menuItem.getId()
            );

            orderItem.setItemName(
                    menuItem.getName()
            );

            orderItem.setUnitPrice(
                    menuItem.getPrice()
            );

            orderItem.setQuantity(
                    requestedItem.quantity()
            );

            orderItem.setLineTotal(
                    lineTotal
            );

            order.addItem(
                    orderItem
            );

            subtotal =
                    subtotal.add(
                            lineTotal
                    );
        }

        if (orderRestaurant == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order must contain at least one menu item"
            );
        }

        order.setRestaurant(
                orderRestaurant
        );

        // ======================================
        // MINIMUM ORDER AMOUNT
        // ======================================

        validateMinimumOrderAmount(
                subtotal,
                settings.minimumOrderAmount()
        );

        // ======================================
        // DELIVERY FEE
        // ======================================

        BigDecimal deliveryFee =
                calculateDeliveryFee(
                        subtotal,
                        settings.defaultDeliveryFee()
                );

        order.setSubtotal(
                subtotal
        );

        // ======================================
        // DISCOUNT
        // ======================================

        DiscountCalculationResult discountResult =
                discountCalculationService
                        .calculateDiscount(
                                order.getItems(),
                                subtotal
                        );

        order.setDiscountAmount(
                discountResult
                        .discountAmount()
        );

        order.setAppliedDiscountNames(
                discountResult
                        .appliedDiscountNames()
        );

        order.setDeliveryFee(
                deliveryFee
        );

        BigDecimal discountedSubtotal =
                subtotal.subtract(
                        discountResult
                                .discountAmount()
                );

        order.setTotalAmount(
                discountedSubtotal
                        .add(
                                deliveryFee
                        )
        );

        CustomerOrder savedOrder =
                orderRepository.save(
                        order
                );

        try {

            emailService
                    .sendOrderConfirmationEmail(
                            savedOrder
                    );

        } catch (Exception exception) {

            LOGGER.error(
                    "Order {} was saved, but its confirmation email failed.",
                    savedOrder.getId(),
                    exception
            );
        }

        return savedOrder;
    }

    // ==========================================
    // ORDER SCHEDULING
    // ==========================================

    private void configureOrderSchedule(
            CustomerOrder order,
            PlaceOrderRequest request
    ) {

        OrderTiming selectedTiming =
                request.orderTiming() == null
                        ? OrderTiming.NOW
                        : request.orderTiming();

        order.setOrderTiming(
                selectedTiming
        );

        if (
                selectedTiming
                        == OrderTiming.NOW
        ) {

            order.setMealSlot(
                    null
            );

            order.setScheduledFor(
                    null
            );

            order.setPreparationStartAt(
                    null
            );

            return;
        }

        if (request.mealSlot() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select breakfast, lunch or dinner"
            );
        }

        if (
                request.scheduledFor()
                        == null
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a scheduled order date and time"
            );
        }

        LocalDateTime minimumScheduleTime =
                LocalDateTime.now()
                        .plusHours(1);

        if (
                request.scheduledFor()
                        .isBefore(
                                minimumScheduleTime
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Scheduled orders require at least one hour of advance notice"
            );
        }

        order.setMealSlot(
                request.mealSlot()
        );

        order.setScheduledFor(
                request.scheduledFor()
        );

        order.setPreparationStartAt(
                request.scheduledFor()
                        .minusMinutes(30)
        );
    }

    // ==========================================
    // PAYMENT
    // ==========================================

    private void configureDemoPayment(
            CustomerOrder order,
            PaymentMethod paymentMethod
    ) {

        if (
                paymentMethod
                        == PaymentMethod.DEMO_RAZORPAY
        ) {

            order.setPaymentStatus(
                    PaymentStatus.PAID
            );

            order.setTransactionId(
                    generateDemoTransactionId()
            );

            return;
        }

        order.setPaymentStatus(
                PaymentStatus.NOT_REQUIRED
        );

        order.setTransactionId(
                null
        );
    }

    private String
            generateDemoTransactionId() {

        String randomValue =
                UUID.randomUUID()
                        .toString()
                        .replace(
                                "-",
                                ""
                        )
                        .substring(
                                0,
                                14
                        )
                        .toUpperCase();

        return "pay_demo_"
                + randomValue;
    }

    // ==========================================
    // DISCOUNT QUOTE
    // ==========================================

    public DiscountQuoteResponse
            calculateDiscountQuote(
                    DiscountQuoteRequest request
            ) {

        PlatformSettingResponse settings =
                getOperationalSettings();

        ensureOrderingAvailable(
                settings
        );

        CustomerOrder temporaryOrder =
                new CustomerOrder();

        BigDecimal subtotal =
                BigDecimal.ZERO;

        Long quoteRestaurantId =
                null;

        for (
                OrderItemRequest requestedItem
                : request.items()
        ) {

            MenuItem menuItem =
                    menuItemRepository
                            .findById(
                                    requestedItem
                                            .menuItemId()
                            )
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "Menu item not found with id: "
                                                    + requestedItem
                                                    .menuItemId()
                                    )
                            );

            if (!menuItem.isAvailable()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Menu item is currently unavailable: "
                                + menuItem.getName()
                );
            }

            if (
                    menuItem.getRestaurant()
                            == null
            ) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Menu item is not assigned to a restaurant: "
                                + menuItem.getName()
                );
            }

            Restaurant menuItemRestaurant =
                    menuItem.getRestaurant();

            if (
                    menuItemRestaurant.getApprovalStatus()
                            != RestaurantApprovalStatus.APPROVED
            ) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Restaurant is not approved for ordering"
                );
            }

            if (!menuItemRestaurant.isActive()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Restaurant is currently unavailable"
                );
            }

            Long menuItemRestaurantId =
                    menuItemRestaurant.getId();

            if (
                    quoteRestaurantId
                            == null
            ) {

                quoteRestaurantId =
                        menuItemRestaurantId;

            } else if (
                    !quoteRestaurantId
                            .equals(
                                    menuItemRestaurantId
                            )
            ) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "All items in an order must belong to the same restaurant"
                );
            }

            BigDecimal lineTotal =
                    menuItem.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            requestedItem
                                                    .quantity()
                                    )
                            );

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setMenuItemId(
                    menuItem.getId()
            );

            orderItem.setItemName(
                    menuItem.getName()
            );

            orderItem.setUnitPrice(
                    menuItem.getPrice()
            );

            orderItem.setQuantity(
                    requestedItem.quantity()
            );

            orderItem.setLineTotal(
                    lineTotal
            );

            temporaryOrder.addItem(
                    orderItem
            );

            subtotal =
                    subtotal.add(
                            lineTotal
                    );
        }

        if (quoteRestaurantId == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order must contain at least one menu item"
            );
        }

        // ======================================
        // MINIMUM ORDER AMOUNT
        // ======================================

        validateMinimumOrderAmount(
                subtotal,
                settings.minimumOrderAmount()
        );

        // ======================================
        // DISCOUNT
        // ======================================

        DiscountCalculationResult discountResult =
                discountCalculationService
                        .calculateDiscount(
                                temporaryOrder
                                        .getItems(),
                                subtotal
                        );

        // ======================================
        // DELIVERY FEE
        // ======================================

        BigDecimal deliveryFee =
                calculateDeliveryFee(
                        subtotal,
                        settings.defaultDeliveryFee()
                );

        BigDecimal totalAmount =
                subtotal
                        .subtract(
                                discountResult
                                        .discountAmount()
                        )
                        .add(
                                deliveryFee
                        );

        return new DiscountQuoteResponse(

                subtotal,

                discountResult
                        .discountAmount(),

                discountResult
                        .appliedDiscountNames(),

                deliveryFee,

                totalAmount
        );
    }

    // ==========================================
    // ALL ORDERS
    // ==========================================

    public List<CustomerOrder>
            getAllOrders() {

        return orderRepository
                .findAllByOrderByCreatedAtDesc();
    }

    // ==========================================
    // CUSTOMER ORDERS
    // ==========================================

    public List<CustomerOrder>
            getOrdersForCustomer(
                    String customerEmail
            ) {

        CustomerAccount customer =
                findCustomerByEmail(
                        customerEmail
                );

        return orderRepository
                .findByCustomerAccountIdOrderByCreatedAtDesc(
                        customer.getId()
                );
    }

    // ==========================================
    // GET ONE ORDER
    // ==========================================

    public CustomerOrder getOrderById(
            Long id
    ) {

        return orderRepository
                .findById(
                        id
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found with id: "
                                        + id
                        )
                );
    }

    // ==========================================
    // CUSTOMER CANCEL ORDER
    // ==========================================

    @Transactional
    public CustomerOrder
            cancelCustomerOrder(
                    Long orderId,
                    String customerEmail
            ) {

        CustomerAccount customer =
                findCustomerByEmail(
                        customerEmail
                );

        CustomerOrder order =
                getOrderById(
                        orderId
                );

        if (
                order.getCustomerAccount()
                        == null
                ||
                !order.getCustomerAccount()
                        .getId()
                        .equals(
                                customer.getId()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot cancel this order"
            );
        }

        if (
                order.getStatus()
                        != OrderStatus.PLACED
                &&
                order.getStatus()
                        != OrderStatus.CONFIRMED
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This order can no longer be cancelled"
            );
        }

        order.setStatus(
                OrderStatus.CANCELLED
        );

        CustomerOrder cancelledOrder =
                orderRepository.save(
                        order
                );

        try {

            emailService
                    .sendOrderCancellationEmail(
                            cancelledOrder
                    );

        } catch (Exception exception) {

            LOGGER.error(
                    "Order {} was cancelled, but its cancellation email failed.",
                    cancelledOrder.getId(),
                    exception
            );
        }

        return cancelledOrder;
    }

    // ==========================================
    // UPDATE ORDER STATUS
    // ==========================================

    @Transactional
    public CustomerOrder updateOrderStatus(
            Long id,
            OrderStatus newStatus
    ) {

        CustomerOrder order =
                getOrderById(
                        id
                );

        order.setStatus(
                newStatus
        );

        CustomerOrder updatedOrder =
                orderRepository.save(
                        order
                );

        try {

            emailService
                    .sendOrderStatusEmail(
                            updatedOrder
                    );

        } catch (Exception exception) {

            LOGGER.error(
                    "Order {} status changed, but its status email failed.",
                    updatedOrder.getId(),
                    exception
            );
        }

        return updatedOrder;
    }

    // ==========================================
    // PLATFORM OPERATIONAL SETTINGS
    // ==========================================

    private PlatformSettingResponse
            getOperationalSettings() {

        return platformSettingService
                .getSettings();
    }

    private void ensureOrderingAvailable(
            PlatformSettingResponse settings
    ) {

        if (
                settings.maintenanceMode()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Ordering is temporarily unavailable due to platform maintenance"
            );
        }
    }

    // ==========================================
    // MINIMUM ORDER VALIDATION
    // ==========================================

    private void validateMinimumOrderAmount(
            BigDecimal subtotal,
            BigDecimal minimumOrderAmount
    ) {

        BigDecimal minimum =
                minimumOrderAmount == null
                        ? BigDecimal.ZERO
                        : minimumOrderAmount;

        if (
                subtotal.compareTo(
                        minimum
                ) < 0
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum order amount is ₹"
                            + minimum
                                    .stripTrailingZeros()
                                    .toPlainString()
            );
        }
    }

    // ==========================================
    // DELIVERY FEE CALCULATION
    // ==========================================

    private BigDecimal calculateDeliveryFee(
            BigDecimal subtotal,
            BigDecimal configuredDeliveryFee
    ) {

        if (
                subtotal.compareTo(
                        FREE_DELIVERY_MINIMUM
                ) >= 0
        ) {

            return BigDecimal.ZERO;
        }

        if (configuredDeliveryFee == null) {

            return BigDecimal.ZERO;
        }

        return configuredDeliveryFee;
    }

    // ==========================================
    // FIND CUSTOMER
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
}