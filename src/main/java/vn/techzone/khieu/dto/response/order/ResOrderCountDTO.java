package vn.techzone.khieu.dto.response.order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Order count grouped by status")
public interface ResOrderCountDTO {
    @Schema(description = "Total order count", example = "100")
    Long getCount();

    @Schema(description = "Pending order count", example = "10")
    Long getCountPending();

    @Schema(description = "Shipping order count", example = "5")
    Long getCountShipping();

    @Schema(description = "Completed order count", example = "85")
    Long getCountCompleted();
}
