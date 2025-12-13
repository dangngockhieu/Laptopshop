package vn.techzone.khieu.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.user.LoginDTO;
import vn.techzone.khieu.dto.request.user.RegisterUserDTO;
import vn.techzone.khieu.dto.request.user.ResetPasswordDTO;
import vn.techzone.khieu.dto.response.user.ResLoginDTO;
import vn.techzone.khieu.entity.User;
import vn.techzone.khieu.mapper.UserMapper;
import vn.techzone.khieu.repository.UserRepository;
import vn.techzone.khieu.service.user.UserPrincipal;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.error.FailRequestException;
import vn.techzone.khieu.utils.error.NotFindException;
import org.springframework.security.core.Authentication;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final SecurityUtil securityUtil;

    @Value("${verify-base-url}")
    private String verifyUrl;

    // AuthController

    public record LoginResult(ResLoginDTO resLoginDTO, String refreshToken) {}

    public LoginResult processLogin(LoginDTO loginDTO) {
        // Nạp input gồm email/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // Lưu thông tin người dùng đã xác thực vào Security Context
        // req.user
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                principal.getId(),
                principal.getName(),
                principal.getEmail(),
                principal.getRole());

        ResLoginDTO res = new ResLoginDTO();
        res.setUser(userInfo);

        // Create a Token
        String accessToken = this.securityUtil.createAccessToken(loginDTO.getEmail(), res.getUser());
        res.setAccessToken(accessToken);

        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getEmail(), res);
        this.updateUserToken(loginDTO.getEmail(), refreshToken);

        // TRẢ VỀ RECORD
        return new LoginResult(res, refreshToken);
    }

    public Optional<User> findByEmailAndRefreshToken(String email, String refreshToken) {
        String hashed = SecurityUtil.hashWithSHA256(refreshToken);
        return this.userRepository.findByEmailAndRefreshToken(email, hashed);
    }

    public void updateUserToken(String email, String refreshToken) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFindException("Không tìm thấy User với email: " + email));
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
                .orElseThrow(() -> new NotFindException("Không tìm thấy User với email: " + email));
        if (!token.equals(user.getVerificationCode())) {
            throw new IllegalArgumentException("Token không hợp lệ");
        }

        if (user.getCodeExpired() == null || Instant.now().isAfter(user.getCodeExpired())) {
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
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put(
                "verifyUrl",
                verifyUrl + "?token=" + token + "&email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8));

        emailService.sendEmailFromTemplate(
                user.getEmail(),
                "Verify your account",
                "verify",
                variables);
    }

    public void handleRegister(RegisterUserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new FailRequestException("Thông tin gửi lên Không hợp lệ");
        }
        String hashPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(hashPassword);
        User user = userMapper.toRegisterUser(userDTO);
        user.setVerified(false);
        user.setRole("USER");
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
                .orElseThrow(() -> new NotFindException("Không tìm thấy User với email: " + email));
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
                .orElseThrow(() -> new NotFindException("Không tìm thấy User với email: " + email));
        if (user.getCodeExpired() != null && Instant.now().isBefore(user.getCodeExpired()))
            throw new BadCredentialsException("Vui lòng gửi lại sau ít phút nữa");
        if (user.getVerified() != null && !user.getVerified())
            throw new BadCredentialsException("Tài khoản chưa được xác minh, không thể reset password");
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setVerificationCode(token);
        Instant codeExpired = Instant.now().plus(30, ChronoUnit.MINUTES);
        user.setCodeExpired(codeExpired);
        this.userRepository.save(user);

        // Gửi email reset password

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("codeID", token);

        emailService.sendEmailFromTemplate(
                user.getEmail(),
                "Reset your password",
                "passwordReset",
                variables);
    }

    public void resetPassword(ResetPasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        User user = this.userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFindException("Không tìm thấy User với email: " + dto.getEmail()));
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
