package vn.techzone.khieu.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for updating a user's role")
public class UpdateRoleDTO {
    @Schema(description = "New user role", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Role không được để trống")
    private String role;
}
