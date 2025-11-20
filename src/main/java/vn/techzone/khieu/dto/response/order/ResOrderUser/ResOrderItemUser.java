package vn.techzone.khieu.dto.response.order.ResOrderUser;

public interface ResOrderItemUser {
    Long getOrderId();

    Long getProductId();

    String getProductName();

    Long getOrderItemId();

    Integer getQuantity();

    Integer getUnitPrice();

    Boolean getReviewed();

    String getImageUrl();
}
