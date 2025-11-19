package vn.techzone.khieu.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.product.CreateProductDTO;
import vn.techzone.khieu.dto.request.product.FilterProductDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.product.ResProductDTO;
import vn.techzone.khieu.dto.response.product.ResCardProductDTO;
import vn.techzone.khieu.entity.Product;
import vn.techzone.khieu.service.ProductService;
import vn.techzone.khieu.utils.annotation.ApiMessage;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping()
    @ApiMessage("Tạo mới sản phẩm")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody CreateProductDTO createProductDTO) {
        Product product = this.productService.createProduct(createProductDTO);
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
    public ResponseEntity<List<ResCardProductDTO>> getTopProducts(
            @RequestParam(value = "category", required = false) String category) {
        return ResponseEntity.ok(this.productService.getTopProducts(category));
    }

    @GetMapping("/filter")
    @ApiMessage("Lọc sản phẩm theo nhiều tiêu chí")
    public ResponseEntity<List<ResCardProductDTO>> filterProducts(FilterProductDTO filterProductDTO) {
        List<ResCardProductDTO> products = this.productService.filterProducts(filterProductDTO);
        return ResponseEntity.ok(products);
    }

}
