package vn.techzone.khieu.dto.response.product.ProductDetailDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product detail projection")
public interface ResProductDetail {
    Long getId();

    String getName();

    Integer getOriginalPrice();

    Integer getPrice();

    Integer getCoupon();

    Integer getQuantity();

    Integer getSold();

    String getWarranty();

    String getInfor();

    String getCpu();

    String getRam();

    String getStorage();

    String getScreen();

    String getGraphicsCard();

    String getBattery();

    String getWeight();

    String getReleaseYear();

    String getCategory();

    String getFactory();

    Double getAvgRating();

    Long getTotalReviews();
}
