package vn.techzone.khieu.dto.response.order.ResOrderUser;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResOrderUserDTO {
    private Long orderId;
    private Integer totalPrice;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant orderDate;
    private String paymentMethod;
    private String paymentStatus;
    private List<OrderItemDTO> products;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDTO {
        private Long productId;
        private String productName;
        private Long orderItemId;
        private Integer quantity;
        private Integer unitPrice;
        private Boolean reviewed;
        private String imageUrl;
    }
}
