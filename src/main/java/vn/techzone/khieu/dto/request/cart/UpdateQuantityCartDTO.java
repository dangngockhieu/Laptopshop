package vn.techzone.khieu.dto.request.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body for updating product quantity in the current user's cart")
public class UpdateQuantityCartDTO {
    @Schema(description = "Product id in cart", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ProductId cannot be null")
    private Long productId;

    @Schema(description = "New quantity", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;
}
