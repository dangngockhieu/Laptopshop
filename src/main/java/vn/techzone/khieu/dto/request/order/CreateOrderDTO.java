package vn.techzone.khieu.dto.request.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDTO {

    @NotEmpty(message = "Recipient Name cannot be empty")
    String recipientName;

    @NotEmpty(message = "Address cannot be empty")
    String address;

    @NotBlank(message = "Phone cannot be empty")
    String phone;

    @NotNull(message = "Total Price cannot be empty")
    Integer totalPrice;

    @NotEmpty(message = "Payment Method cannot be empty")
    String paymentMethod;
    OrderItemDTO[] items;

    @Data
    public static class OrderItemDTO {
        Long productId;
        Integer quantity;
        Integer price;
    }
}
