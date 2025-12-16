package vn.techzone.khieu.dto.request.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request body for creating a product review")
public class CreateReviewDTO {
    @Schema(description = "Reviewed product id", example = "1")
    Long productId;

    @Schema(description = "Order item id proving the purchase", example = "10")
    Long orderItemId;

    @Schema(description = "Rating score", example = "5")
    Integer rating;

    @Schema(description = "Review comment", example = "Fast laptop, good screen and battery.")
    String comment;
}
