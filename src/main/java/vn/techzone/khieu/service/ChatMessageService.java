package vn.techzone.khieu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

// Import thư viện chuẩn của Jackson
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.techzone.khieu.entity.ChatMessage;
import vn.techzone.khieu.repository.ChatMessageRepository;
import vn.techzone.khieu.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public void save(Long userId, String content, String role, Object productData) {
        ChatMessage message = new ChatMessage();
        message.setUser(userRepository.getReferenceById(userId));
        message.setContent(content);
        message.setRole(role);

        if (productData != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                message.setProductData(mapper.writeValueAsString(productData));
            } catch (Exception e) {
                message.setProductData(null);
            }
        }

        chatMessageRepository.save(message);
    }

    public List<ChatMessage> getHistory(Long userId) {
        return chatMessageRepository.findByUserIdOrderByCreatedAtAsc(userId);
    }
}