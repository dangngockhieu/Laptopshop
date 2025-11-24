package vn.techzone.khieu.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequestDTO {
    @NotBlank(message = "Vui lòng nhập câu hỏi!")
    private String question;
}
