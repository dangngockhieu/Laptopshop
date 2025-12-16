package vn.techzone.khieu.dto.request.order;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body for moving an order to shipping status")
public class UpdateToShippingDTO {
    @Schema(description = "Shipping tracking code", example = "GHN123456789", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Tracking code is required")
    private String trackingCode;

    @Schema(description = "Expected delivery date", example = "2026-09-20", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Expected date is required")
    private LocalDate expectedDate;

}
