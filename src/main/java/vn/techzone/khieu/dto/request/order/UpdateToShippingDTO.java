package vn.techzone.khieu.dto.request.order;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateToShippingDTO {
    @NotBlank(message = "Tracking code is required")
    private String trackingCode;

    @NotNull(message = "Expected date is required")
    private LocalDate expectedDate;

}
