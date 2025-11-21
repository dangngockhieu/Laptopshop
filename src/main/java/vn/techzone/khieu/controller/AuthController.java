package vn.techzone.khieu.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.user.EmailDTO;
import vn.techzone.khieu.dto.request.user.LoginDTO;
import vn.techzone.khieu.dto.request.user.RegisterUserDTO;
import vn.techzone.khieu.dto.request.user.ResetPasswordDTO;
import vn.techzone.khieu.dto.response.user.ResLoginDTO;
import vn.techzone.khieu.dto.response.user.ResStringDTO;
import vn.techzone.khieu.entity.User;
import vn.techzone.khieu.service.AuthService;
import vn.techzone.khieu.service.UserService;
import vn.techzone.khieu.service.user.UserPrincipal;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;
import vn.techzone.khieu.utils.error.NotFindException;
import vn.techzone.khieu.utils.error.UnauthorizedException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
        private final UserService userService;
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final AuthService authService;

        @Value("${refresh-token-validity-in-seconds}")
        private long refreshTokenExpired;

        @PostMapping("/login")
        @ApiMessage("Đăng nhập")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
                // Nạp input gồm email/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getEmail(), loginDTO.getPassword());
                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);
                // Lưu thông tin người dùng đã xác thực vào Security Context
                // req.user
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // Create a Token
                UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
                ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                                principal.getId(),
                                principal.getName(),
                                principal.getEmail(),
                                principal.getRole());
                ResLoginDTO res = new ResLoginDTO();
                res.setUser(userInfo);

                String accessToken = this.securityUtil.createAccessToken(loginDTO.getEmail(), res.getUser());
                res.setAccessToken(accessToken);

                String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getEmail(), res);
                this.authService.updateUserToken(loginDTO.getEmail(), refreshToken);

                ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                                .httpOnly(true)
                                // .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpired)
                                .sameSite("Strict")
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                .body(res);
        }

        @GetMapping("/account")
        @ApiMessage("Thông tin tài khoản")
        public ResponseEntity<ResLoginDTO.UserInfo> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().orElse(null);
                User user = this.userService.findUserByEmail(email)
                                .orElseThrow(() -> new NotFindException(
                                                "Không tìm thấy User với email: " + email));

                ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole());

                return ResponseEntity.ok(userInfo);
        }

        @PostMapping("/refresh-token")
        @ApiMessage("Refresh token")
        public ResponseEntity<ResLoginDTO> refreshToken(
                        @CookieValue(name = "refreshToken", required = false) String refreshToken) {
                if (refreshToken == null || refreshToken.isEmpty())
                        throw new UnauthorizedException("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");

                Jwt decodedToken = this.securityUtil.checkValidToken(refreshToken);
                String email = decodedToken.getSubject();

                User currentUser = this.authService.findByEmailAndRefreshToken(email, refreshToken)
                                .orElseThrow(() -> new UnauthorizedException(
                                                "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại"));

                ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                                currentUser.getId(),
                                currentUser.getName(),
                                currentUser.getEmail(),
                                currentUser.getRole());
                ResLoginDTO res = new ResLoginDTO();
                res.setUser(userInfo);

                String accessToken = this.securityUtil.createAccessToken(email, res.getUser());
                res.setAccessToken(accessToken);

                String newRefreshToken = this.securityUtil.createRefreshToken(email, res);
                this.authService.updateUserToken(email, newRefreshToken);

                ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                                .httpOnly(true)
                                // .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpired)
                                .sameSite("Strict")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                .body(res);
        }

        @PostMapping("/logout")
        @ApiMessage("Logout")
        public ResponseEntity<Void> handleLogout() {
                String email = SecurityUtil.getCurrentUserLogin().orElse(null);
                if (email != null) {
                        this.authService.updateUserToken(email, null);
                }
                ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                                .httpOnly(true)
                                // .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                                .build();

        }

        @PostMapping("/register")
        @ApiMessage("Đăng ký")
        public ResponseEntity<ResStringDTO> register(@Valid @RequestBody RegisterUserDTO registerDTO) {
                this.authService.handleRegister(registerDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                new ResStringDTO(
                                                "Đăng ký thành công! Vui lòng kiểm tra email để xác minh tài khoản trong 30 phút."));
        }

        @GetMapping("/verify")
        @ApiMessage("Xác minh tài khoản")
        public ResponseEntity<String> verifyAccount(@RequestParam String token, @RequestParam String email) {
                this.authService.verifyToken(token, email);
                try {
                        ClassPathResource resource = new ClassPathResource("static/verifySuccess.html");
                        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                        return ResponseEntity.ok()
                                        .contentType(MediaType.TEXT_HTML)
                                        .body(html);
                } catch (IOException e) {
                        throw new RuntimeException("Failed to load verify success page", e);
                }
        }

        @PostMapping("/send-reset-password")
        @ApiMessage("Gửi email đặt lại mật khẩu")
        public ResponseEntity<ResStringDTO> sendResetPasswordEmail(@RequestBody EmailDTO emailDTO) {
                this.authService.sendEmailResetPassword(emailDTO.getEmail());
                return ResponseEntity
                                .ok(new ResStringDTO("Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư."));
        }

        @PatchMapping("/reset-password")
        @ApiMessage("Đặt lại mật khẩu")
        public ResponseEntity<ResStringDTO> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
                this.authService.resetPassword(resetPasswordDTO);
                return ResponseEntity.ok(new ResStringDTO("Mật khẩu đã được đặt lại thành công!"));
        }

        @PostMapping("/send-verification-email")
        @ApiMessage("Gửi email xác minh")
        public ResponseEntity<ResStringDTO> sendVerificationEmail(@RequestBody EmailDTO emailDTO) {
                this.authService.reSendVerificationEmail(emailDTO.getEmail());
                return ResponseEntity.ok(new ResStringDTO("Email xác minh đã được gửi. Vui lòng kiểm tra hộp thư."));
        }
}
