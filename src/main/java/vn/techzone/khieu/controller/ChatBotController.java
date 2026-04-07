package vn.techzone.khieu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.product.ChatRequestDTO;
import vn.techzone.khieu.dto.response.product.AllProductForChatBot.ChatResponseDTO;
import vn.techzone.khieu.entity.ChatMessage;
import vn.techzone.khieu.service.ChatBotService;
import vn.techzone.khieu.service.ChatMessageService;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;
import vn.techzone.khieu.utils.annotation.RateLimit;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "7. AI Chatbot", description = "Tích hợp AI tư vấn khách hàng và lưu trữ lịch sử trò chuyện")
public class ChatBotController {
    private final ChatBotService chatBotService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/ask")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @RateLimit(capacity = 2, minutes = 1)
    @ApiMessage("Chat với AI")
    public ResponseEntity<ChatResponseDTO> ask(@RequestBody ChatRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        ChatResponseDTO response = chatBotService.generateChatResponse(dto.getQuestion());

        if (userId != null) {
            chatMessageService.save(userId, dto.getQuestion(), "USER", null);
            chatMessageService.save(userId, response.getReplyMessage(), "AI", response.getSuggestedProducts());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Lấy lịch sử chat")
    public ResponseEntity<List<ChatMessage>> getHistory() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(chatMessageService.getHistory(userId));
    }
}
