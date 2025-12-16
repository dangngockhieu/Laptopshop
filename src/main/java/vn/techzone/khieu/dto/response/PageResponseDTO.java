package vn.techzone.khieu.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Generic paginated response")
public class PageResponseDTO<T> {
    @Schema(description = "Current page data")
    private List<T> data;

    @Schema(description = "Total matching records", example = "100")
    private long totalElements;

    @Schema(description = "Total pages", example = "10")
    private int totalPages;

    @Schema(description = "Current page number", example = "1")
    private int currentPage;

    @Schema(description = "Page size", example = "10")
    private int pageSize;
}
