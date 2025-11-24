package vn.techzone.khieu.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.techzone.khieu.dto.response.product.AllProductForChatBot.ChatResponseDTO;
import vn.techzone.khieu.dto.response.product.AllProductForChatBot.ResProductforAiChatBotDTO;

@Service
@RequiredArgsConstructor
public class ChatBotService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final RestClient restClient = RestClient.create();
    private final ProductService productService;

    public ChatResponseDTO generateChatResponse(String question) {
        List<ResProductforAiChatBotDTO> products = productService.getAllProductsForChatBot();

        String contextString = products.stream()
                .map(p -> {
                    // Nếu price null thì lấy originalPrice, nếu cả 2 null thì gán 0
                    Integer finalPrice = p.getPrice() != null ? p.getPrice()
                            : (p.getOriginalPrice() != null ? p.getOriginalPrice() : 0);
                    // Xử lý null cho imageUrl
                    String imageUrl = p.getImageUrl() != null ? p.getImageUrl() : "Chưa có ảnh";

                    return "id:%d name:%s price:%d image:%s category:%s cpu:%s ram:%s storage:%s screen:%s battery:%s features:%s"
                            .formatted(p.getId(), p.getName(), finalPrice, imageUrl, p.getCategory(),
                                    p.getCpu(), p.getRam(), p.getStorage(), p.getScreen(),
                                    p.getBattery(), p.getFeatures());
                })
                .collect(Collectors.joining("\n"));

        String prompt = """
                VAI TRÒ: Bạn là Bitu - Trợ lý ảo bán hàng.
                CONTEXT: %s
                USER: "%s"
                NHIỆM VỤ:
                1. Trả lời thân thiện (reply_message).
                2. Tìm sản phẩm phù hợp (suggested_products).
                YÊU CẦU ĐẦU RA (Chỉ trả về JSON hợp lệ, tuyệt đối không dùng markdown block như ```json):
                {
                    "reply_message": "Câu trả lời của bạn...",
                    "suggested_products": [
                        {
                            "id": "...",
                            "name": "...",
                            "price": 0,
                            "image": "...",
                            "reason": "Lý do ngắn gọn..."
                        }
                    ]
                }
                """.formatted(contextString, question);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0.7));

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey;

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            List<?> candidates = (List<?>) response.get("candidates");
            Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) candidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> part = (Map<?, ?>) parts.get(0);
            String text = (String) part.get("text");

            if (text != null) {
                text = text.trim();
                if (text.startsWith("```json")) {
                    text = text.substring(7);
                } else if (text.startsWith("```")) {
                    text = text.substring(3);
                }
                if (text.endsWith("```")) {
                    text = text.substring(0, text.length() - 3);
                }
                text = text.trim();
            }

            return mapper.readValue(text, ChatResponseDTO.class);

        } catch (Exception e) {
            System.err.println("AI Error: " + e.getMessage());
            e.printStackTrace(); // In ra log chi tiết để dễ debug hơn nếu có lỗi
            return new ChatResponseDTO("Bitu đang bận xíu, bạn hỏi lại sau nhé!", List.of());
        }
    }
}