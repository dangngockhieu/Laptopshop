package vn.techzone.khieu.dto.request.user;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private String name;
    private String password;
    private String role;
}
