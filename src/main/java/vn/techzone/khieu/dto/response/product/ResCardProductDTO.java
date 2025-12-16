package vn.techzone.khieu.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Compact product card response")
public interface ResCardProductDTO {
    @Schema(description = "Product id", example = "1")
    Long getId();

    @Schema(description = "Product name", example = "MacBook Pro 14 M3")
    String getName();

    @Schema(description = "Original listed price", example = "49990000")
    Integer getOriginalPrice();

    @Schema(description = "Current price", example = "44990000")
    Integer getPrice();

    @Schema(description = "Discount percentage", example = "10")
    Integer getCoupon();

    @Schema(description = "Average rating", example = "4.8")
    Double getAvgRating();

    @Schema(description = "Total review count", example = "25")
    Long getTotalReviews();

    @Schema(description = "Product thumbnail URL")
    String getImageUrl();
}
