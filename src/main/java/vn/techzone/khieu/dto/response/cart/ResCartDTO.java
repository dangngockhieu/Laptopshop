package vn.techzone.khieu.dto.response.cart;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cart item response")
public interface ResCartDTO {
    @Schema(description = "Product id", example = "1")
    Long getId();

    @Schema(description = "Product name", example = "MacBook Pro 14 M3")
    String getName();

    @Schema(description = "Current price", example = "44990000")
    Integer getPrice();

    @Schema(description = "Available stock quantity", example = "20")
    Integer getQuantity();

    @Schema(description = "Original listed price", example = "49990000")
    Integer getOriginalPrice();

    @Schema(description = "Quantity in cart", example = "2")
    Integer getNumber();

    @Schema(description = "Whether this item is selected for checkout", example = "true")
    Boolean getSelected();

    @Schema(description = "Product thumbnail URL")
    String getImageUrl();
}
