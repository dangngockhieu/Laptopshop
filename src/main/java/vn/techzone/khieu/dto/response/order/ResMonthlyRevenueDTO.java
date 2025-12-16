package vn.techzone.khieu.dto.response.order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Revenue total for a month")
public interface ResMonthlyRevenueDTO {
    @Schema(description = "Month number", example = "9")
    Integer getMonth();

    @Schema(description = "Revenue amount", example = "125000000")
    Long getRevenue();
}
