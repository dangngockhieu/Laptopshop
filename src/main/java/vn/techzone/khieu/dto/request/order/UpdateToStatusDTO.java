package vn.techzone.khieu.dto.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "Request body for updating an order status")
public class UpdateToStatusDTO {
    @Schema(description = "New order status", example = "DELIVERED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Status is required")
    String status;
}
