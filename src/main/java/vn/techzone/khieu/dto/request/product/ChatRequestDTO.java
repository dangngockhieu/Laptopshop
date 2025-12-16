package vn.techzone.khieu.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for asking the AI chatbot")
public class ChatRequestDTO {
    @Schema(description = "Customer question", example = "Can you suggest a laptop for programming under 25 million VND?", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Vui lòng nhập câu hỏi!")
    private String question;
}
