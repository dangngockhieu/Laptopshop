package vn.techzone.khieu.dto.response.order.ResRevenue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResRevenueThisMonthDTO {
    private Long currentMonthRevenue;
    private Double growth;
}
