package vn.techzone.khieu.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Best-selling product summary")
public interface ResBestSeller {
    @Schema(description = "Product id", example = "1")
    Long getId();

    @Schema(description = "Product name", example = "MacBook Pro 14 M3")
    String getName();

    @Schema(description = "Sold quantity", example = "120")
    Long getSold();

}
