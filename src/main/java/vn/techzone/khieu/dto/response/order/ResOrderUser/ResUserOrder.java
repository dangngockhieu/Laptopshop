package vn.techzone.khieu.dto.response.order.ResOrderUser;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User order projection")
public interface ResUserOrder {
    @Schema(description = "Order id", example = "1")
    Long getOrderId();

    @Schema(description = "Total order price", example = "25000000")
    Integer getTotalPrice();

    @Schema(description = "Order status", example = "PENDING")
    String getStatus();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    Instant getOrderDate();

    @Schema(description = "Payment method", example = "COD")
    String getPaymentMethod();

    @Schema(description = "Payment status", example = "UNPAID")
    String getPaymentStatus();

}
