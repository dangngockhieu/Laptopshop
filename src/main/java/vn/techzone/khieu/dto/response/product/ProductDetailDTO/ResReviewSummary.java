package vn.techzone.khieu.dto.response.product.ProductDetailDTO;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Product review summary response")
public class ResReviewSummary {
    @Schema(description = "Average rating", example = "4.8")
    private Double avgRating;
    @Schema(description = "Total review count", example = "25")
    private Long totalReviews;
    @Schema(description = "Review items")
    private List<ResReview> items;
}
