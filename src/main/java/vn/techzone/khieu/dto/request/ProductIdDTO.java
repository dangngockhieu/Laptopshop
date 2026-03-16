package vn.techzone.khieu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductIdDTO {
    @NotNull(message = "ProductId cannot be null")
    private Long productId;
}
