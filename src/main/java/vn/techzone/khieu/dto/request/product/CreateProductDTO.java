package vn.techzone.khieu.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body for creating a new product")
public class CreateProductDTO {

    @Schema(description = "Product name", example = "MacBook Pro 14 M3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Schema(description = "Original listed price", example = "49990000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "OriginalPrice cannot be empty")
    private Integer originalPrice;

    @Schema(description = "Discount percentage", example = "10")
    private Integer coupon;

    @Schema(description = "Available stock quantity", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Quantity cannot be empty")
    private Integer quantity;

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

    @Schema(description = "Product category", example = "Laptop", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Category cannot be empty")
    private String category;

    @Schema(description = "Manufacturer or brand", example = "Apple", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Factory cannot be empty")
    private String factory;
}
