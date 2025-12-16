package vn.techzone.khieu.dto.response.product.ProductDetailDTO;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product review response")
public interface ResReview {
    Long getId();

    Integer getRating();

    String getComment();

    Instant getCreatedAt();

    String getUserName();
}
