package vn.techzone.khieu.dto.request.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateQuantityCartDTO {
    @NotNull(message = "ProductId cannot be null")
    private Long productId;
    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;
}
