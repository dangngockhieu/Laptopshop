package vn.techzone.khieu.dto.response.product.AllProductForChatBot;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDTO {
    @JsonProperty("reply_message")
    private String replyMessage;

    @JsonProperty("suggested_products")
    private List<ProductSuggestion> suggestedProducts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductSuggestion {
        private String id;
        private String name;
        private Integer price;
        private String image;
        private String reason;
    }
}
