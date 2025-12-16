package vn.techzone.khieu.dto.response.order.ResRevenue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Revenue comparison projection")
public interface ResRevenue {
    @Schema(description = "Current month revenue", example = "125000000")
    Long getCurrentMonthRevenue();

    @Schema(description = "Previous month revenue", example = "100000000")
    Long getPrevMonthRevenue();
}
