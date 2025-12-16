package vn.techzone.khieu.dto.response.order.ResOrderUser;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Order response for the current user")
public class ResOrderUserDTO {
    @Schema(description = "Order id", example = "1")
    private Long orderId;
    @Schema(description = "Total order price", example = "25000000")
    private Integer totalPrice;
    @Schema(description = "Order status", example = "PENDING")
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    @Schema(description = "Order creation date time")
    private Instant orderDate;
    @Schema(description = "Payment method", example = "COD")
    private String paymentMethod;
    @Schema(description = "Payment status", example = "UNPAID")
    private String paymentStatus;
    @Schema(description = "Products in this order")
    private List<OrderItemDTO> products;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Order product item for user order response")
    public static class OrderItemDTO {
        @Schema(description = "Product id", example = "1")
        private Long productId;
        @Schema(description = "Product name", example = "MacBook Pro 14 M3")
        private String productName;
        @Schema(description = "Order item id", example = "10")
        private Long orderItemId;
        @Schema(description = "Ordered quantity", example = "1")
        private Integer quantity;
        @Schema(description = "Unit price at order time", example = "25000000")
        private Integer unitPrice;
        @Schema(description = "Whether this item has been reviewed", example = "false")
        private Boolean reviewed;
        @Schema(description = "Product image URL")
        private String imageUrl;
    }
}
