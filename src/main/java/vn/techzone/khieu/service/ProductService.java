package vn.techzone.khieu.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.product.CreateProductDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.product.ResProductDTO;
import vn.techzone.khieu.dto.response.product.ResCardProductDTO;
import vn.techzone.khieu.entity.Product;
import vn.techzone.khieu.mapper.ProductMapper;
import vn.techzone.khieu.repository.ProductRepository;
import vn.techzone.khieu.utils.GenericSpecification;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Product createProduct(CreateProductDTO createProductDTO) {
        Product product = productMapper.toCreateProduct(createProductDTO);
        return this.productRepository.save(product);
    }

    public PageResponseDTO<ResProductDTO> getAllProducts(Pageable pageable, String keyword) {
        Specification<Product> spec = null;

        if (keyword != null && !keyword.isBlank()) {
            Specification<Product> nameSpec = GenericSpecification.like("name", keyword);

            spec = spec == null ? nameSpec : spec.and(nameSpec);
        }
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<ResProductDTO> products = productPage.getContent().stream()
                .map(productMapper::toResProductDTO)
                .collect(Collectors.toList());

        return new PageResponseDTO<ResProductDTO>(
                products,
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.getNumber() + 1,
                productPage.getSize());
    }

    public List<ResCardProductDTO> getTopProducts(String category) {
        List<ResCardProductDTO> products = productRepository.findAllProducts(category);
        return products;
    }

}
