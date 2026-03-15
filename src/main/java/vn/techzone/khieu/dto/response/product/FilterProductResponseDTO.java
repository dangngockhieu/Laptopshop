package vn.techzone.khieu.dto.response.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FilterProductResponseDTO {
    private List<ResCardProductDTO> products;
    private long count;
}
