package vn.techzone.khieu.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.product.CreateProductDTO;
import vn.techzone.khieu.dto.request.product.FilterProductDTO;
import vn.techzone.khieu.dto.request.product.UpdateProductDTO;
import vn.techzone.khieu.dto.request.review.CreateReviewDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.product.ResProductDTO;
import vn.techzone.khieu.dto.response.product.ProductDetailDTO.ResProductDetailDTO;
import vn.techzone.khieu.dto.response.user.ResStringDTO;
import vn.techzone.khieu.dto.response.product.FilterProductResponseDTO;
import vn.techzone.khieu.dto.response.product.ResCardProductDTO;
import vn.techzone.khieu.entity.Product;
import vn.techzone.khieu.service.ProductService;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;
import vn.techzone.khieu.utils.error.StorageException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Tạo mới sản phẩm")
    public ResponseEntity<Product> createProduct(
            @RequestPart("data") @Valid CreateProductDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images)
            throws URISyntaxException, IOException, StorageException {
        Product product = this.productService.createProduct(dto, images);
        return ResponseEntity.ok(product);
    }

    @GetMapping()
    @ApiMessage("Lấy danh sách sản phẩm")
    public ResponseEntity<PageResponseDTO<ResProductDTO>> getAllUsers(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(current - 1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        PageResponseDTO<ResProductDTO> products = this.productService.getAllProducts(pageable, keyword);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/top-products")
    @ApiMessage("Lấy danh sách sản phẩm bán chạy")
    public ResponseEntity<List<ResCardProductDTO>> getTopProducts() {
        return ResponseEntity.ok(this.productService.getTopProducts());
    }

    @GetMapping("/top-products-category")
    @ApiMessage("Lấy danh sách sản phẩm bán chạy theo danh mục")
    public ResponseEntity<List<ResCardProductDTO>> getTopProductsCategory(
            @RequestParam(value = "category") String category) {
        return ResponseEntity.ok(this.productService.getTopProductsCategory(category));
    }

    @PostMapping("/filter")
    @ApiMessage("Filter products by multiple criteria")
    public ResponseEntity<FilterProductResponseDTO> filterProducts(
            @Valid @RequestBody FilterProductDTO filterProductDTO) {
        FilterProductResponseDTO response = this.productService
                .filterProducts(filterProductDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reviews")
    @ApiMessage("Tạo đánh giá cho sản phẩm")
    public ResponseEntity<ResStringDTO> createReview(@Valid @RequestBody CreateReviewDTO createReviewDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        productService.createReview(userId, createReviewDTO);
        return ResponseEntity.ok(new ResStringDTO("Đánh giá đã được tạo thành công"));
    }

    @PatchMapping("/{id}")
    @ApiMessage("Cập nhật thông tin sản phẩm")
    public ResponseEntity<ResProductDTO> updateProduct(@Valid @RequestBody UpdateProductDTO updateProductDTO,
            @PathVariable Long id) {
        ResProductDTO product = this.productService.updateProduct(id, updateProductDTO);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy chi tiết sản phẩm")
    public ResponseEntity<ResProductDetailDTO> getProductById(@PathVariable Long id) {
        ResProductDetailDTO product = this.productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

}
