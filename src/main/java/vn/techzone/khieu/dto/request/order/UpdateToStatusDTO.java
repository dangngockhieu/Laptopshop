package vn.techzone.khieu.dto.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateToStatusDTO {
    @NotNull(message = "Order ID is required")
    private Long orderId;
    String status;
}
