package vn.techzone.khieu.dto.response.order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Order item response")
public interface ResOrderItemDTO {
    @Schema(description = "Product id", example = "1")
    Long getProdocuctId();

    @Schema(description = "Product name", example = "MacBook Pro 14 M3")
    String getName();

    @Schema(description = "Ordered quantity", example = "1")
    Integer getQuantity();

    @Schema(description = "Unit price at order time", example = "25000000")
    Integer getUnitPrice();

    @Schema(description = "Product image URL")
    String getImageUrl();
}
