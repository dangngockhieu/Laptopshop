package vn.techzone.khieu.dto.response.product.ProductDetailDTO;

import java.util.List;

import lombok.Data;

@Data
public class ResProductDetailDTO {
    private ResProductDetail product;
    private List<String> imageUrls;
    private ResReviewSummary reviews;
}
