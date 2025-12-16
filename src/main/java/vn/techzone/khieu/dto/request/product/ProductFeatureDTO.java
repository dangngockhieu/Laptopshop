package vn.techzone.khieu.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request body for linking a feature to a product")
public class ProductFeatureDTO {
    @Schema(description = "Feature id", example = "1")
    private Long featureId;
}
