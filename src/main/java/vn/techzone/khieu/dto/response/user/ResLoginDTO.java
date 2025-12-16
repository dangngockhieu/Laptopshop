package vn.techzone.khieu.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login response with access token and user information")
public class ResLoginDTO {
    @Schema(description = "JWT access token")
    private String accessToken;

    @Schema(description = "Authenticated user information")
    private UserInfo user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Authenticated user profile")
    public static class UserInfo {
        @Schema(description = "User id", example = "1")
        private long id;

        @Schema(description = "User full name", example = "Nguyen Van A")
        private String name;

        @Schema(description = "User email address", example = "user@example.com")
        private String email;

        @Schema(description = "User role", example = "USER")
        private String role;
    }
}
