package vn.techzone.khieu.dto.request.order;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body for creating an order from selected cart items")
public class CreateOrderDTO {

    @Schema(description = "Recipient full name", example = "Nguyen Van A", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Recipient Name cannot be empty")
    String recipientName;

    @Schema(description = "Shipping address", example = "12 Nguyen Hue, District 1, Ho Chi Minh City", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Address cannot be empty")
    String address;

    @Schema(description = "Recipient phone number", example = "0901234567", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone cannot be empty")
    String phone;

    @Schema(description = "Total order price", example = "25000000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Total Price cannot be empty")
    Integer totalPrice;

    @Schema(description = "Payment method", example = "COD", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Payment Method cannot be empty")
    String paymentMethod;

    @ArraySchema(schema = @Schema(description = "Order item detail"))
    OrderItemDTO[] items;

    @Data
    @Schema(description = "Product item included in the order")
    public static class OrderItemDTO {
        @Schema(description = "Product id", example = "1")
        Long productId;

        @Schema(description = "Quantity ordered", example = "1")
        Integer quantity;

        @Schema(description = "Unit price at order time", example = "25000000")
        Integer price;
    }
}
