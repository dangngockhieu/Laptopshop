package vn.techzone.khieu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.user.LoginDTO;
import vn.techzone.khieu.dto.response.user.ResLoginDTO;
import vn.techzone.khieu.service.user.UserPrincipal;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    @PostMapping("/login")
    @ApiMessage("Đăng nhập thành công")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Nạp input gồm email/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword());
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Lưu thông tin người dùng đã xác thực vào Security Context
        // req.user
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Create a Token
        String access_token = this.securityUtil.createToken(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                principal.getId(),
                principal.getName(),
                principal.getEmail(),
                principal.getRole());
        ResLoginDTO res = new ResLoginDTO();
        res.setAccess_token(access_token);
        res.setUser(userInfo);
        return ResponseEntity.ok().body(res);
    }
}
