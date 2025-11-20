package vn.techzone.khieu.dto.response.product.ProductDetailDTO;

import java.time.Instant;

public interface ResReview {
    Long getId();

    Integer getRating();

    String getComment();

    Instant getCreatedAt();

    String getUserName();
}
