package vn.techzone.khieu.dto.response.product.AllProductForChatBot;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "AI chatbot response")
public class ChatResponseDTO {
    @Schema(description = "AI reply message")
    @JsonProperty("reply_message")
    private String replyMessage;

    @Schema(description = "Products suggested by the AI")
    @JsonProperty("suggested_products")
    private List<ProductSuggestion> suggestedProducts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Product suggested by the AI chatbot")
    public static class ProductSuggestion {
        @Schema(description = "Product id", example = "1")
        private String id;
        @Schema(description = "Product name", example = "MacBook Pro 14 M3")
        private String name;
        @Schema(description = "Current price", example = "44990000")
        private Integer price;
        @Schema(description = "Product image URL")
        private String image;
        @Schema(description = "Reason why this product was suggested")
        private String reason;
    }
}
