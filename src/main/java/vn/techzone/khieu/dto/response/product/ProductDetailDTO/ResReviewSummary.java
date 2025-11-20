package vn.techzone.khieu.dto.response.product.ProductDetailDTO;

import java.util.List;

import lombok.Data;

@Data
public class ResReviewSummary {
    private Double avgRating;
    private Long totalReviews;
    private List<ResReview> items;
}
