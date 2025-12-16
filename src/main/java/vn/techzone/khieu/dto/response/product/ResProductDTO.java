package vn.techzone.khieu.dto.response.product;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product management response")
public class ResProductDTO {

    @Schema(description = "Product id", example = "1")
    private Long id;
    @Schema(description = "Product name", example = "MacBook Pro 14 M3")
    private String name;
    @Schema(description = "Original listed price", example = "49990000")
    private Integer originalPrice;
    @Schema(description = "Current price", example = "44990000")
    private Integer price;
    @Schema(description = "Discount percentage", example = "10")
    private Integer coupon;
    @Schema(description = "Available stock quantity", example = "20")
    private Integer quantity;
    @Schema(description = "Sold quantity", example = "5")
    private Integer sold;
    private String warranty;
    private String infor;
    private String cpu;
    private String ram;
    private String storage;
    private String screen;
    private String graphicsCard;
    private String battery;
    private String weight;
    private String releaseYear;
    private String category;
    private String factory;
    private List<Images> images;
    @Schema(description = "Feature ids linked to the product")
    private List<Long> features;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Product image response")
    public static class Images {
        @Schema(description = "Image id", example = "1")
        private Long id;
        @Schema(description = "Image URL")
        private String url;
    }
}
