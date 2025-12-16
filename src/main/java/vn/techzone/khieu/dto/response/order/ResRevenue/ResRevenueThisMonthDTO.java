package vn.techzone.khieu.dto.response.order.ResRevenue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Current month revenue and growth response")
public class ResRevenueThisMonthDTO {
    @Schema(description = "Current month revenue", example = "125000000")
    private Long currentMonthRevenue;
    @Schema(description = "Growth percentage compared with previous month", example = "25.0")
    private Double growth;
}
