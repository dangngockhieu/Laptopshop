package vn.techzone.khieu.dto.request.order;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateToShippingDTO {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Tracking code is required")
    private String trackingCode;

    @NotNull(message = "Expected date is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant expectedDate;

}
