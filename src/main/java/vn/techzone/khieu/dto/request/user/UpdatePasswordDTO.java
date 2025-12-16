package vn.techzone.khieu.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request body for changing the current user's password")
public class UpdatePasswordDTO {
    @Schema(description = "Current password", example = "OldPassword1!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Old password is not empty")
    private String oldPassword;

    @Schema(description = "New password with uppercase, lowercase, number and special character", example = "NewPassword1!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "New password is not empty")
    @Size(min = 8, max = 32, message = "Password must be 8-32 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "Password must contain uppercase, lowercase, number, special character")
    private String newPassword;

    @Schema(description = "Password confirmation", example = "NewPassword1!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Confirm password is not empty")
    private String confirmPassword;
}
