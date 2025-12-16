package vn.techzone.khieu.dto.response.order;

import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Admin order list item response")
public interface ResOrderDTO {
    @Schema(description = "Order id", example = "1")
    Long getOrderId();

    @Schema(description = "User id", example = "1")
    Long getUserId();

    @Schema(description = "Recipient full name", example = "Nguyen Van A")
    String getRecipientName();

    @Schema(description = "Shipping address")
    String getAdderss();

    @Schema(description = "Recipient phone number", example = "0901234567")
    String getPhone();

    @Schema(description = "Total order price", example = "25000000")
    Integer getTotalPrice();

    @Schema(description = "Order status", example = "PENDING")
    String getOrderStatus();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    Instant getOrderDate();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    Instant getDeliveryDate();

    @Schema(description = "Expected delivery date", example = "2026-09-20")
    LocalDate getExpectedDate();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    Instant getReceivedDate();

    @Schema(description = "Shipping tracking code", example = "GHN123456789")
    String getTrackingCode();

    @Schema(description = "Payment method", example = "COD")
    String getPaymentMethod();

    @Schema(description = "Payment status", example = "UNPAID")
    String getPaymentStatus();

    @Schema(description = "Customer email", example = "user@example.com")
    String getUserEmail();
}
