package vn.techzone.khieu.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
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
import vn.techzone.khieu.utils.error.UnauthorizedException;

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

                String accessToken = this.securityUtil.createAccessToken(loginDTO.getEmail(), res.getUser());
                res.setAccessToken(accessToken);

                String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getEmail(), res);
                this.userService.updateUserToken(loginDTO.getEmail(), refreshToken);

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
                                .orElseThrow(() -> new NotFoundUserException(
                                                "Không tìm thấy User với email: " + email));

                ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole());

                return ResponseEntity.ok(userInfo);
        }

        @GetMapping("/refresh")
        @ApiMessage("Refresh token")
        public ResponseEntity<ResLoginDTO> refreshToken(
                        @CookieValue(name = "refreshToken", required = false) String refreshToken) {
                if (refreshToken == null || refreshToken.isEmpty())
                        throw new UnauthorizedException("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");

                Jwt decodedToken = this.securityUtil.checkValidToken(refreshToken);
                String email = decodedToken.getSubject();

                User currentUser = this.userService.findByEmailAndRefreshToken(email, refreshToken)
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
                this.userService.updateUserToken(email, newRefreshToken);

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
                        this.userService.updateUserToken(email, null);
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

}
