package vn.techzone.khieu.dto.response.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResLoginDTO {
    private String access_token;
}
