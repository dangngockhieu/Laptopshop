package vn.techzone.khieu.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.user.LoginDTO;
import vn.techzone.khieu.dto.response.user.ResLoginDTO;
import vn.techzone.khieu.entity.User;
import vn.techzone.khieu.service.UserService;
import vn.techzone.khieu.service.user.UserPrincipal;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;
import vn.techzone.khieu.utils.error.NotFoundUserException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
        private final UserService userService;
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;

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
                String access_token = this.securityUtil.createAccessToken(authentication, res.getUser());
                res.setAccess_token(access_token);
                String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getEmail(), res);
                this.userService.updateUserToken(loginDTO.getEmail(), refresh_token);
                ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refresh_token)
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
                String email = SecurityUtil.getCurrentUserLogin().orElse("");
                User user = this.userService.findUserByEmail(email)
                                .orElseThrow(() -> new NotFoundUserException(
                                                "Không tìm thấy User với email: " + email));

                ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole());

                return ResponseEntity.ok(userInfo);
        }
}
