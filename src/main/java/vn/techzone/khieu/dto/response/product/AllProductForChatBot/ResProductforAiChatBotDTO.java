package vn.techzone.khieu.dto.response.product.AllProductForChatBot;

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
@Schema(description = "Product data prepared for AI chatbot context")
public class ResProductforAiChatBotDTO {

    @Schema(description = "Product id", example = "1")
    private Long id;
    @Schema(description = "Product name", example = "MacBook Pro 14 M3")
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
    @Schema(description = "Feature names linked to the product")
    private List<String> features;
}
