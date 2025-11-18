package vn.techzone.khieu.dto.response.product;

public interface ResCardProductDTO {
    Long getId();

    String getName();

    Integer getOriginalPrice();

    Integer getPrice();

    Integer getCoupon();

    Double getAvgRating();

    Long getTotalReviews();

    String getUrlImage();
}
