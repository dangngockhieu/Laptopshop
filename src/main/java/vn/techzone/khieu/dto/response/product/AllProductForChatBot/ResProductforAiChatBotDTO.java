package vn.techzone.khieu.dto.response.product.AllProductForChatBot;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResProductforAiChatBotDTO {

    private Long id;
    private String name;
    private Integer originalPrice;
    private Integer price;
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
    private String imageUrl;
    private List<String> features;
}
