package vn.techzone.khieu.dto.response.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private String role;
    private boolean verified;
}
