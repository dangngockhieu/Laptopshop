package vn.techzone.khieu.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import vn.techzone.khieu.dto.request.product.ProductFeatureDTO;
import vn.techzone.khieu.dto.request.product.UpdateProductDTO;
import vn.techzone.khieu.dto.request.review.CreateReviewDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.product.ResProductDTO;
import vn.techzone.khieu.dto.response.product.ProductDetailDTO.ResProductDetailDTO;
import vn.techzone.khieu.dto.response.user.ResStringDTO;
import vn.techzone.khieu.dto.response.product.FilterProductResponseDTO;
import vn.techzone.khieu.dto.response.product.ResBestSeller;
import vn.techzone.khieu.dto.response.product.ResCardProductDTO;
import vn.techzone.khieu.entity.Product;
import vn.techzone.khieu.service.ProductExcelService;
import vn.techzone.khieu.service.ProductService;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;
import vn.techzone.khieu.utils.error.StorageException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductExcelService productExcelService;

    @PostMapping(value = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Tạo mới sản phẩm")
    public ResponseEntity<Product> createProduct(
            @RequestPart("data") @Valid CreateProductDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images)
            throws URISyntaxException, IOException, StorageException {
        Product product = this.productService.createProduct(dto, images);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/admin/upload-excel")
    public ResponseEntity<ResStringDTO> uploadExcel(@RequestParam("excel") MultipartFile file) {
        // Kiểm tra file trống
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResStringDTO("Vui lòng chọn một file excel!"));
        }

        // Kiểm tra định dạng file (optional)
        String contentType = file.getContentType();
        if (contentType == null
                || (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        && !contentType.equals("application/vnd.ms-excel"))) {
            return ResponseEntity.badRequest()
                    .body(new ResStringDTO("Chỉ chấp nhận file định dạng Excel (.xlsx, .xls)"));
        }

        try {
            // Gọi service xử lý (Đọc -> Lưu DB -> Xóa file)
            productExcelService.importFromExcel(file);
            return ResponseEntity.ok(new ResStringDTO("File excel đã được xử lý thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ResStringDTO("Lỗi khi xử lý file: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/products-paginate")
    @ApiMessage("Lấy danh sách sản phẩm")
    public ResponseEntity<PageResponseDTO<ResProductDTO>> getAllProducts(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category") String category,
            @RequestParam(value = "factory", required = false) String factory) {
        Pageable pageable = PageRequest.of(current - 1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        PageResponseDTO<ResProductDTO> products = this.productService.getAllProducts(pageable, keyword, category,
                factory);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/admin/top-products")
    @ApiMessage("Lấy danh sách sản phẩm bán chạy")
    public ResponseEntity<List<ResBestSeller>> getTopProducts() {
        return ResponseEntity.ok(this.productService.getTopProducts());
    }

    @GetMapping("/admin/count")
    @ApiMessage("Đếm số lượng sản phẩm có trong kho")
    public ResponseEntity<Long> countProducts() {
        Long count = this.productService.countProducts();
        return ResponseEntity.ok(count);
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

    @PostMapping("/user/reviews")
    @ApiMessage("Tạo đánh giá cho sản phẩm")
    public ResponseEntity<ResStringDTO> createReview(@Valid @RequestBody CreateReviewDTO createReviewDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        productService.createReview(userId, createReviewDTO);
        return ResponseEntity.ok(new ResStringDTO("Đánh giá đã được tạo thành công"));
    }

    @DeleteMapping("/admin/{id}")
    @ApiMessage("Xóa sản phẩm")
    public ResponseEntity<ResStringDTO> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ResStringDTO("Sản phẩm đã được xóa thành công"));
    }

    @PostMapping(value = "/admin/product-images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Thêm ảnh sản phẩm")
    public ResponseEntity<ResStringDTO> addProductImages(
            @PathVariable Long id,
            @RequestPart("images") List<MultipartFile> images)
            throws IOException, StorageException, URISyntaxException {
        productService.addProductImages(id, images);
        return ResponseEntity.ok().body(new ResStringDTO("Ảnh sản phẩm đã được thêm thành công"));
    }

    @DeleteMapping("/admin/product-image/{imageId}")
    @ApiMessage("Xóa ảnh sản phẩm")
    public ResponseEntity<ResStringDTO> deleteProductImage(@PathVariable Long imageId)
            throws StorageException, URISyntaxException {
        productService.deleteProductImage(imageId);
        return ResponseEntity.ok().body(new ResStringDTO("Ảnh sản phẩm đã được xóa thành công"));
    }

    // Add features to product
    @PostMapping("/admin/{productId}/features")
    @ApiMessage("Thêm tính năng cho sản phẩm")
    public ResponseEntity<ResStringDTO> addFeaturesToProduct(
            @PathVariable Long productId,
            @Valid @RequestBody List<ProductFeatureDTO> featureDTOs) {
        productService.addFeaturesToProduct(productId, featureDTOs);
        return ResponseEntity.ok(new ResStringDTO("Tính năng đã được thêm vào sản phẩm thành công"));
    }

    @DeleteMapping("/admin/{productId}/features/{featureId}")
    @ApiMessage("Xóa tính năng khỏi sản phẩm")
    public ResponseEntity<ResStringDTO> deleteFeatureFromProduct(
            @PathVariable Long productId,
            @PathVariable Long featureId) {
        productService.deleteFeatureFromProduct(productId, featureId);
        return ResponseEntity.ok(new ResStringDTO("Tính năng đã được xóa khỏi sản phẩm thành công"));
    }

    ////////////////////////////////////////////////////////////////////////////////////
    @PatchMapping("/admin/{id}")
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
