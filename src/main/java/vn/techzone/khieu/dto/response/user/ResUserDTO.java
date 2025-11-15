package vn.techzone.khieu.dto.response.user;

import lombok.Data;

@Data
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private String role;
    private boolean verified;
}
