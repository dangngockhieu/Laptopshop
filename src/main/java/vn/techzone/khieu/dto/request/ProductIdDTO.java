package vn.techzone.khieu.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body containing a product id")
public class ProductIdDTO {
    @Schema(description = "Product id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ProductId cannot be null")
    private Long productId;
}
