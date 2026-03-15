package vn.techzone.khieu.dto.request.product;

import lombok.Data;

@Data
public class UpdateProductDTO {

    private String name;

    private Integer originalPrice;

    private Integer coupon;

    private Integer quantity;

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
}
