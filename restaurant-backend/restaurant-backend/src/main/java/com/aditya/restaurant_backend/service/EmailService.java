package com.aditya.restaurant_backend.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.OrderItem;
import com.aditya.restaurant_backend.entity.OrderTiming;
import com.aditya.restaurant_backend.entity.PaymentMethod;
import com.aditya.restaurant_backend.entity.PaymentStatus;

@Service
public class EmailService {

    private final String mailUsername;
private final String frontendBaseUrl;
private final String resendApiKey;
private final String resendFromEmail;
public EmailService(
        @Value("${spring.mail.username}")
        String mailUsername,
        @Value("${app.frontend.base-url}")
        String frontendBaseUrl,
        @Value("${resend.api-key}")
        String resendApiKey,
        @Value("${resend.from-email}")
        String resendFromEmail
) {
    this.mailUsername = mailUsername;
    this.frontendBaseUrl = frontendBaseUrl;
    this.resendApiKey = resendApiKey;
    this.resendFromEmail = resendFromEmail;
}

    public void sendPasswordResetEmail(
            String customerEmail,
            String customerName,
            String resetToken
    ) {
        String resetLink =
                frontendBaseUrl
                        + "/reset-password?token="
                        + resetToken;

        SimpleMailMessage message =
                createMessage(
                        customerEmail,
                        "Reset your SpiceRoute password"
                );

        message.setText(
                "Hello "
                        + customerName
                        + ",\n\n"
                        + "We received a request to reset "
                        + "your SpiceRoute password.\n\n"
                        + "Use this secure link to create "
                        + "a new password:\n"
                        + resetLink
                        + "\n\n"
                        + "This link expires in 15 minutes "
                        + "and can only be used once.\n\n"
                        + "If you did not request this, "
                        + "you can ignore this email.\n\n"
                        + "SpiceRoute Restaurant"
        );

        sendWithResend(message);
    }

    public void sendOrderConfirmationEmail(
            CustomerOrder order
    ) {
        if (order.getCustomerAccount() == null) {
            return;
        }

        SimpleMailMessage message =
                createMessage(
                        order.getCustomerAccount()
                                .getEmail(),
                        "SpiceRoute Order #"
                                + order.getId()
                                + " confirmed"
                );

        message.setText(
                "Hello "
                        + order.getCustomerName()
                        + ",\n\n"
                        + "Thank you for your order!\n\n"
                        + createOrderSummary(order)
                        + "\nYour order has been placed "
                        + "successfully.\n\n"
                        + "Track your order here:\n"
                        + frontendBaseUrl
                        + "/my-orders\n\n"
                        + "SpiceRoute Restaurant"
        );

        sendWithResend(message);
    }

  public void sendOrderStatusEmail(
        CustomerOrder order
) {
    if (order.getCustomerAccount() == null) {
        return;
    }

    String readableStatus =
            order.getStatus()
                    .name()
                    .replace("_", " ");

    String reviewSection = "";

    if (order.getStatus()
            == com.aditya.restaurant_backend.entity.OrderStatus.DELIVERED) {

        String reviewLink =
                frontendBaseUrl
                        + "/review?orderId="
                        + order.getId();

        reviewSection =
                "\n\nHow was your SpiceRoute experience?\n\n"
                        + "Food quality: ☆ ☆ ☆ ☆ ☆\n"
                        + "Customer service: ☆ ☆ ☆ ☆ ☆\n\n"
                        + "Give your review using this secure link:\n"
                        + reviewLink
                        + "\n\n"
                        + "Only the customer who placed this order "
                        + "can submit its review.";
    }

    SimpleMailMessage message =
            createMessage(
                    order.getCustomerAccount()
                            .getEmail(),
                    "Order #"
                            + order.getId()
                            + " status: "
                            + readableStatus
            );

    message.setText(
            "Hello "
                    + order.getCustomerName()
                    + ",\n\n"
                    + "Your SpiceRoute order #"
                    + order.getId()
                    + " is now:\n\n"
                    + readableStatus
                    + "\n\n"
                    + createScheduleSummary(order)
                    + "\n"
                    + createPaymentSummary(order)
                    + reviewSection
                    + "\n\nTrack the latest progress here:\n"
                    + frontendBaseUrl
                    + "/my-orders\n\n"
                    + "SpiceRoute Restaurant"
    );

    sendWithResend(message);
}

    public void sendOrderCancellationEmail(
            CustomerOrder order
    ) {
        if (order.getCustomerAccount() == null) {
            return;
        }

        SimpleMailMessage message =
                createMessage(
                        order.getCustomerAccount()
                                .getEmail(),
                        "Order #"
                                + order.getId()
                                + " cancelled"
                );

        message.setText(
                "Hello "
                        + order.getCustomerName()
                        + ",\n\n"
                        + "Your SpiceRoute order #"
                        + order.getId()
                        + " has been cancelled.\n\n"
                        + "Cancelled order total: "
                        + formatPrice(
                                order.getTotalAmount()
                        )
                        + "\n\n"
                        + createDiscountSummary(order)
                        + createScheduleSummary(order)
                        + "\n"
                        + createPaymentSummary(order)
                        + "\nYou can place another order at:\n"
                        + frontendBaseUrl
                        + "\n\n"
                        + "SpiceRoute Restaurant"
        );

        sendWithResend(message);
    }
        public void sendSupportTicketCreatedEmail(
            Long ticketId,
            String customerName,
            String customerEmail,
            String subject
    ) {
        // Confirmation email to customer
        SimpleMailMessage customerMessage =
                createMessage(
                        customerEmail,
                        "SpiceRoute Support Ticket #"
                                + ticketId
                                + " created"
                );

        customerMessage.setText(
                "Hello "
                        + customerName
                        + ",\n\n"
                        + "Your support ticket has been created successfully.\n\n"
                        + "Ticket number: #"
                        + ticketId
                        + "\n"
                        + "Subject: "
                        + subject
                        + "\n\n"
                        + "Our support team will review your request "
                        + "and reply as soon as possible.\n\n"
                        + "View your support tickets here:\n"
                        + frontendBaseUrl
                        + "/support\n\n"
                        + "SpiceRoute Restaurant"
        );

        sendWithResend(customerMessage);


        // Notification email to restaurant/admin
        SimpleMailMessage adminMessage =
                createMessage(
                        mailUsername,
                        "New SpiceRoute Support Ticket #"
                                + ticketId
                );

        adminMessage.setText(
                "A new customer support ticket has been created.\n\n"
                        + "Ticket number: #"
                        + ticketId
                        + "\n"
                        + "Customer: "
                        + customerName
                        + "\n"
                        + "Customer email: "
                        + customerEmail
                        + "\n"
                        + "Subject: "
                        + subject
                        + "\n\n"
                        + "Open the admin support dashboard:\n"
                        + frontendBaseUrl
                        + "/admin/support\n\n"
                        + "SpiceRoute Restaurant"
        );

        sendWithResend(adminMessage);
    }


    public void sendSupportAdminReplyEmail(
            Long ticketId,
            String customerName,
            String customerEmail,
            String subject,
            String reply
    ) {
        SimpleMailMessage message =
                createMessage(
                        customerEmail,
                        "New reply on SpiceRoute Support Ticket #"
                                + ticketId
                );

        message.setText(
                "Hello "
                        + customerName
                        + ",\n\n"
                        + "Our support team has replied to your ticket.\n\n"
                        + "Ticket number: #"
                        + ticketId
                        + "\n"
                        + "Subject: "
                        + subject
                        + "\n\n"
                        + "Support reply:\n"
                        + reply
                        + "\n\n"
                        + "View the complete conversation here:\n"
                        + frontendBaseUrl
                        + "/support\n\n"
                        + "SpiceRoute Restaurant"
        );

        sendWithResend(message);
    }


    public void sendSupportCustomerReplyEmail(
            Long ticketId,
            String customerName,
            String customerEmail,
            String subject,
            String reply
    ) {
        SimpleMailMessage message =
                createMessage(
                        mailUsername,
                        "Customer replied to Support Ticket #"
                                + ticketId
                );

        message.setText(
                "A customer has replied to a support ticket.\n\n"
                        + "Ticket number: #"
                        + ticketId
                        + "\n"
                        + "Customer: "
                        + customerName
                        + "\n"
                        + "Customer email: "
                        + customerEmail
                        + "\n"
                        + "Subject: "
                        + subject
                        + "\n\n"
                        + "Customer reply:\n"
                        + reply
                        + "\n\n"
                        + "Open the admin support dashboard:\n"
                        + frontendBaseUrl
                        + "/admin/support\n\n"
                        + "SpiceRoute Restaurant"
        );

        sendWithResend(message);
    }


    public void sendSupportStatusChangedEmail(
            Long ticketId,
            String customerName,
            String customerEmail,
            String subject,
            String status
    ) {
        String readableStatus =
                status.replace("_", " ");

        SimpleMailMessage message =
                createMessage(
                        customerEmail,
                        "Support Ticket #"
                                + ticketId
                                + " status: "
                                + readableStatus
                );

        message.setText(
                "Hello "
                        + customerName
                        + ",\n\n"
                        + "The status of your SpiceRoute support ticket "
                        + "has been updated.\n\n"
                        + "Ticket number: #"
                        + ticketId
                        + "\n"
                        + "Subject: "
                        + subject
                        + "\n"
                        + "New status: "
                        + readableStatus
                        + "\n\n"
                        + "View your ticket here:\n"
                        + frontendBaseUrl
                        + "/support\n\n"
                        + "SpiceRoute Restaurant"
        );

        sendWithResend(message);
    }
    // ==========================================
// RESTAURANT APPROVAL / ADMIN CREDENTIALS
// ==========================================

public void sendRestaurantApprovalEmail(
        String ownerEmail,
        String ownerName,
        String restaurantName,
        String temporaryPassword
) {

    String adminLoginUrl =
            frontendBaseUrl
                    + "/admin";

    SimpleMailMessage message =
            createMessage(
                    ownerEmail,
                    "Your SpiceRoute restaurant has been approved"
            );

    message.setText(
            "Hello "
                    + ownerName
                    + ",\n\n"

                    + "Congratulations! Your restaurant application "
                    + "has been approved by the SpiceRoute Super Admin.\n\n"

                    + "Restaurant: "
                    + restaurantName
                    + "\n\n"

                    + "Your Restaurant Admin account is now active.\n\n"

                    + "ADMIN LOGIN DETAILS\n"
                    + "------------------------------\n"
                    + "Login URL: "
                    + adminLoginUrl
                    + "\n"

                    + "User ID / Email: "
                    + ownerEmail
                    + "\n"

                    + "Temporary Password: "
                    + temporaryPassword
                    + "\n"
                    + "------------------------------\n\n"

                    + "For security, you must change this temporary "
                    + "password after your first login.\n\n"

                    + "After changing your password, you can manage "
                    + "your restaurant menu, categories, orders, "
                    + "offers, reviews, sales and other restaurant "
                    + "operations from your admin dashboard.\n\n"

                    + "Welcome to SpiceRoute!\n\n"

                    + "SpiceRoute Team"
    );

    sendWithResend(
            message
    );
}
    private SimpleMailMessage createMessage(
            String recipient,
            String subject
    ) {
        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(resendFromEmail);
        message.setTo(recipient);
        message.setSubject(subject);

        return message;
    }
private void sendWithResend(
        SimpleMailMessage message
) {
    try {
        String recipient =
                message.getTo()[0];

        String subject =
                message.getSubject();

        String text =
                message.getText();

        String json =
                "{"
                        + "\"from\":\""
                        + escapeJson(resendFromEmail)
                        + "\","
                        + "\"to\":[\""
                        + escapeJson(recipient)
                        + "\"],"
                        + "\"subject\":\""
                        + escapeJson(subject)
                        + "\","
                        + "\"text\":\""
                        + escapeJson(text)
                        + "\""
                        + "}";

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.resend.com/emails"
                                )
                        )
                        .header(
                                "Authorization",
                                "Bearer " + resendApiKey
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        json,
                                        StandardCharsets.UTF_8
                                )
                        )
                        .build();

        HttpResponse<String> response =
                HttpClient.newHttpClient()
                        .send(
                                request,
                                HttpResponse.BodyHandlers.ofString()
                        );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IllegalStateException(
                    "Resend email failed. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body()
            );
        }

    } catch (
            IOException
                    | InterruptedException exception
    ) {
        if (exception instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }

        throw new IllegalStateException(
                "Could not send email through Resend",
                exception
        );
    }
}

private String escapeJson(
        String value
) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "\\r")
            .replace("\n", "\\n")
            .replace("\t", "\\t");
}
    private String createOrderSummary(
            CustomerOrder order
    ) {
        StringBuilder summary =
                new StringBuilder();

        summary.append("Order number: #")
                .append(order.getId())
                .append("\n\nItems:\n");

        for (OrderItem item
                : order.getItems()) {

            summary.append(
                            item.getQuantity()
                    )
                    .append(" x ")
                    .append(
                            item.getItemName()
                    )
                    .append(" - ")
                    .append(
                            formatPrice(
                                    item.getLineTotal()
                            )
                    )
                    .append("\n");
        }

        summary.append(
                        "\nDelivery address:\n"
                )
                .append(
                        order.getDeliveryAddress()
                )
                .append("\n\nSubtotal: ")
                .append(
                        formatPrice(
                                order.getSubtotal()
                        )
                )
                .append("\n")
                .append(
                        createDiscountSummary(
                                order
                        )
                )
                .append("\nDelivery fee: ")
                .append(
                        formatDeliveryFee(
                                order.getDeliveryFee()
                        )
                )
                .append("\nTotal: ")
                .append(
                        formatPrice(
                                order.getTotalAmount()
                        )
                )
                .append("\n\n")
                .append(
                        createScheduleSummary(
                                order
                        )
                )
                .append("\n")
                .append(
                        createPaymentSummary(
                                order
                        )
                );

        return summary.toString();
    }

    private String createDiscountSummary(
            CustomerOrder order
    ) {
        if (order.getDiscountAmount() == null
                || order.getDiscountAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {
            return "";
        }

        StringBuilder discountSummary =
                new StringBuilder();

        discountSummary.append(
                        "Discount: -"
                )
                .append(
                        formatPrice(
                                order.getDiscountAmount()
                        )
                )
                .append("\n");

        if (order.getAppliedDiscountNames()
                != null
                && !order.getAppliedDiscountNames()
                .isBlank()) {

            discountSummary.append(
                            "Applied offer: "
                    )
                    .append(
                            order.getAppliedDiscountNames()
                    )
                    .append("\n");
        }

        return discountSummary.toString();
    }

    private String createScheduleSummary(
            CustomerOrder order
    ) {
        if (order.getOrderTiming()
                != OrderTiming.SCHEDULED) {
            return "Order timing: Order now\n";
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM yyyy, hh:mm a"
                );

        StringBuilder scheduleSummary =
                new StringBuilder();

        scheduleSummary.append(
                        "Order timing: Scheduled\n"
                )
                .append("Meal: ")
                .append(
                        order.getMealSlot() == null
                                ? "Not specified"
                                : order.getMealSlot()
                                .name()
                                .replace("_", " ")
                )
                .append("\n");

        if (order.getScheduledFor() != null) {
            scheduleSummary.append(
                            "Scheduled delivery: "
                    )
                    .append(
                            order.getScheduledFor()
                                    .format(formatter)
                    )
                    .append("\n");
        }

        if (order.getPreparationStartAt()
                != null) {
            scheduleSummary.append(
                            "Preparation begins: "
                    )
                    .append(
                            order.getPreparationStartAt()
                                    .format(formatter)
                    )
                    .append("\n");
        }

        return scheduleSummary.toString();
    }

    private String createPaymentSummary(
            CustomerOrder order
    ) {
        StringBuilder paymentSummary =
                new StringBuilder();

        if (order.getPaymentMethod()
                == PaymentMethod.DEMO_RAZORPAY) {

            paymentSummary.append(
                            "Payment method: Razorpay Demo\n"
                    )
                    .append(
                            "Payment status: "
                    )
                    .append(
                            order.getPaymentStatus()
                                    == PaymentStatus.PAID
                                    ? "PAID (DEMO)"
                                    : formatPaymentStatus(
                                            order
                                    )
                    )
                    .append("\n");

            if (order.getTransactionId()
                    != null
                    && !order.getTransactionId()
                            .isBlank()) {

                paymentSummary.append(
                                "Demo transaction ID: "
                        )
                        .append(
                                order.getTransactionId()
                        )
                        .append("\n");
            }

            paymentSummary.append(
                    "Important: This was a simulated "
                            + "payment. No real money was charged.\n"
            );

            return paymentSummary.toString();
        }

        paymentSummary.append(
                        "Payment method: Cash on delivery\n"
                )
                .append(
                        "Payment status: Pay on delivery\n"
                );

        return paymentSummary.toString();
    }

    private String formatPaymentStatus(
            CustomerOrder order
    ) {
        if (order.getPaymentStatus() == null) {
            return "NOT AVAILABLE";
        }

        return order.getPaymentStatus()
                .name()
                .replace("_", " ");
    }

    private String formatDeliveryFee(
            BigDecimal deliveryFee
    ) {
        if (deliveryFee == null
                || deliveryFee.compareTo(
                        BigDecimal.ZERO
                ) == 0) {

            return "Free";
        }

        return formatPrice(deliveryFee);
    }

    private String formatPrice(
            BigDecimal amount
    ) {
        if (amount == null) {
            return "₹0.00";
        }

        return "₹" + amount.toPlainString();
    }
}
