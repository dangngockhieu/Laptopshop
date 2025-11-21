package vn.techzone.khieu.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRoleDTO {
    @NotBlank(message = "Role không được để trống")
    private String role;
}