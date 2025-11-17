package vn.techzone.khieu.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.user.CreateUserDTO;
import vn.techzone.khieu.dto.request.user.ResetPasswordDTO;
import vn.techzone.khieu.entity.User;
import vn.techzone.khieu.mapper.UserMapper;
import vn.techzone.khieu.repository.UserRepository;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.error.NotFoundUserException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserMapper userMapper;

    @Value("${verify-base-url}")
    private String verifyUrl;

    // AuthController
    public Optional<User> findByEmailAndRefreshToken(String email, String refreshToken) {
        String hashed = SecurityUtil.hashWithSHA256(refreshToken);
        return this.userRepository.findByEmailAndRefreshToken(email, hashed);
    }

    public void updateUserToken(String email, String refreshToken) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException("Không tìm thấy User với email: " + email));
        if (refreshToken == null) {
            user.setRefreshToken(null);
        } else {
            String hashedToken = SecurityUtil.hashWithSHA256(refreshToken);
            user.setRefreshToken(hashedToken);
        }
        this.userRepository.save(user);
    }

    // Xác minh email và xác thực người dùng
    public void verifyToken(String token, String email) {
        if (token == null)
            throw new BadCredentialsException("Token missing");
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException("Không tìm thấy User với email: " + email));
        if (!token.equals(user.getVerificationCode())) {
            throw new IllegalArgumentException("Token không hợp lệ");
        }

        if (user.getCodeExpired() == null || Instant.now().isAfter(user.getCodeExpired())) {
            this.userRepository.delete(user);
            throw new IllegalArgumentException("Token đã hết hạn");
        }
        user.setVerified(true);
        user.setVerificationCode(null);
        user.setCodeExpired(null);
        user.setSentAt(null);
        this.userRepository.save(user);
    }

    // Các phương thức hỗ trợ gửi Email
    private void sendVerifyEmail(User user, String token) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("verifyUrl", verifyUrl + "?token=" + token + "&email=" + user.getEmail());
        String html = emailService.loadTemplate("verify.html", variables);
        emailService.sendEmailFromTemplate(user.getEmail(), "Verify your account", html);
    }

    public void handleRegister(CreateUserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Thông tin gửi lên Không hợp lệ");
        }
        String hashPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(hashPassword);
        User user = userMapper.toUser(userDTO);
        user.setVerified(false);
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setVerificationCode(token);
        Instant codeExpired = Instant.now().plus(30, ChronoUnit.MINUTES);
        Instant sentAt = Instant.now();
        user.setCodeExpired(codeExpired);
        user.setSentAt(sentAt);
        this.userRepository.save(user);

        // Gửi email xác minh
        sendVerifyEmail(user, token);
    }

    // Gửi lại Email xác thực
    public void reSendVerificationEmail(String email) {
        if (email == null)
            throw new BadCredentialsException("Email is required");
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException("Không tìm thấy User với email: " + email));
        if (user.getCodeExpired() != null && Instant.now().isBefore(user.getCodeExpired()))
            throw new BadCredentialsException("Vui lòng gửi lại sau ít phút nữa");
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setVerificationCode(token);
        Instant codeExpired = Instant.now().plus(30, ChronoUnit.MINUTES);
        Instant sentAt = Instant.now();
        user.setCodeExpired(codeExpired);
        user.setSentAt(sentAt);
        this.userRepository.save(user);

        // Gửi email xác minh
        sendVerifyEmail(user, token);
    }

    public void sendEmailResetPassword(String email) {
        if (email == null)
            throw new BadCredentialsException("Email is required");
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException("Không tìm thấy User với email: " + email));
        if (user.getCodeExpired() != null && Instant.now().isBefore(user.getCodeExpired()))
            throw new BadCredentialsException("Vui lòng gửi lại sau ít phút nữa");
        if (user.getVerified() == false)
            throw new BadCredentialsException("Tài khoản chưa được xác minh, không thể reset password");
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setVerificationCode(token);
        Instant codeExpired = Instant.now().plus(30, ChronoUnit.MINUTES);
        user.setCodeExpired(codeExpired);
        this.userRepository.save(user);

        // Gửi email reset password
        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        String html = emailService.loadTemplate("resetPassword.html", variables);
        this.emailService.sendEmailFromTemplate(user.getEmail(), "Reset your password", html);
    }

    public void resetPassword(ResetPasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        User user = this.userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFoundUserException("Không tìm thấy User với email: " + dto.getEmail()));
        if (!dto.getCode().equals(user.getVerificationCode())) {
            throw new IllegalArgumentException("Code không hợp lệ");
        }
        if (user.getCodeExpired() == null || Instant.now().isAfter(user.getCodeExpired())) {
            throw new IllegalArgumentException("Code đã hết hạn");
        }
        String hashPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(hashPassword);
        user.setVerificationCode(null);
        user.setCodeExpired(null);
        user.setSentAt(null);
        this.userRepository.save(user);
    }
}
