package vn.techzone.khieu.dto.response.order.ResOrderUser;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User order item projection")
public interface ResOrderItemUser {
    @Schema(description = "Order id", example = "1")
    Long getOrderId();

    @Schema(description = "Product id", example = "1")
    Long getProductId();

    @Schema(description = "Product name", example = "MacBook Pro 14 M3")
    String getProductName();

    @Schema(description = "Order item id", example = "10")
    Long getOrderItemId();

    @Schema(description = "Ordered quantity", example = "1")
    Integer getQuantity();

    @Schema(description = "Unit price at order time", example = "25000000")
    Integer getUnitPrice();

    @Schema(description = "Whether this item has been reviewed", example = "false")
    Boolean getReviewed();

    @Schema(description = "Product image URL")
    String getImageUrl();
}
