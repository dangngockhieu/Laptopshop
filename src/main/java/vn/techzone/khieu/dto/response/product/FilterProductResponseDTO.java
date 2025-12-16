package vn.techzone.khieu.dto.response.product;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Filtered product result with matching count")
public class FilterProductResponseDTO {
    @Schema(description = "Filtered product list")
    private List<ResCardProductDTO> products;

    @Schema(description = "Total matching products", example = "42")
    private long count;
}
