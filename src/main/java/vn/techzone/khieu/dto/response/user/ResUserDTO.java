package vn.techzone.khieu.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "User information response")
public class ResUserDTO {
    @Schema(description = "User id", example = "1")
    private long id;

    @Schema(description = "User full name", example = "Nguyen Van A")
    private String name;

    @Schema(description = "User email address", example = "user@example.com")
    private String email;

    @Schema(description = "User role", example = "USER")
    private String role;

    @Schema(description = "Whether the email has been verified", example = "true")
    private boolean verified;
}
