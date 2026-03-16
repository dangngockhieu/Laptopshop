package vn.techzone.khieu.dto.request.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProductDTO {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "OriginalPrice cannot be empty")
    private Integer originalPrice;

    private Integer coupon;

    @NotNull(message = "Quantity cannot be empty")
    private Integer quantity;

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

    @NotEmpty(message = "Category cannot be empty")
    private String category;

    @NotEmpty(message = "Factory cannot be empty")
    private String factory;
}
