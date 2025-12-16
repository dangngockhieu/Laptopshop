package vn.techzone.khieu.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request body for partially updating product information")
public class UpdateProductDTO {

    @Schema(description = "Product name", example = "MacBook Pro 14 M3")
    private String name;

    @Schema(description = "Original listed price", example = "49990000")
    private Integer originalPrice;

    @Schema(description = "Discount percentage", example = "10")
    private Integer coupon;

    @Schema(description = "Available stock quantity", example = "20")
    private Integer quantity;

    @Schema(description = "Sold quantity", example = "5")
    private Integer sold;

    @Schema(description = "Warranty policy", example = "12 months")
    private String warranty;

    @Schema(description = "Product overview or long description")
    private String infor;

    @Schema(description = "CPU information", example = "Apple M3 Pro")
    private String cpu;

    @Schema(description = "RAM information", example = "18GB")
    private String ram;

    @Schema(description = "Storage information", example = "512GB SSD")
    private String storage;

    @Schema(description = "Screen information", example = "14.2 inch Liquid Retina XDR")
    private String screen;

    @Schema(description = "Graphics card information", example = "Integrated GPU")
    private String graphicsCard;

    @Schema(description = "Battery information", example = "70Wh")
    private String battery;

    @Schema(description = "Product weight", example = "1.61kg")
    private String weight;

    @Schema(description = "Release year", example = "2024")
    private String releaseYear;

    @Schema(description = "Product category", example = "Laptop")
    private String category;

    @Schema(description = "Manufacturer or brand", example = "Apple")
    private String factory;
}
