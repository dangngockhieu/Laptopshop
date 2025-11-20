package vn.techzone.khieu.dto.request.review;

import lombok.Data;

@Data
public class CreateReviewDTO {
    Long productId;
    Long orderItemId;
    Integer rating;
    String comment;
}
