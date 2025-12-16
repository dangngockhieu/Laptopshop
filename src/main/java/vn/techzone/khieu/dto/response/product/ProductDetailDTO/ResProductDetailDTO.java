package vn.techzone.khieu.dto.response.product.ProductDetailDTO;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Product detail response with images and review summary")
public class ResProductDetailDTO {
    @Schema(description = "Product detail")
    private ResProductDetail product;
    @Schema(description = "Product image URLs")
    private List<String> imageUrls;
    @Schema(description = "Review summary")
    private ResReviewSummary reviews;
}
