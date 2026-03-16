package vn.techzone.khieu.dto.request.order;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateToStatusDTO {
    @NotEmpty(message = "Status is required")
    String status;
}
