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
@Schema(description = "Simple message response")
public class ResStringDTO {
    @Schema(description = "Response message", example = "Success")
    private String message;
}
