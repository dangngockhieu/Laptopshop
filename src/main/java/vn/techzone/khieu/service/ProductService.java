package vn.techzone.khieu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.product.CreateProductDTO;
import vn.techzone.khieu.dto.request.product.FilterProductDTO;
import vn.techzone.khieu.dto.request.product.UpdateProductDTO;
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
    private final EntityManager entityManager;

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

    public ResProductDTO updateProduct(Long id, UpdateProductDTO updateProductDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (updateProductDTO.getName() != null)
            product.setName(updateProductDTO.getName());
        if (updateProductDTO.getOriginalPrice() != null)
            product.setOriginalPrice(updateProductDTO.getOriginalPrice());
        if (updateProductDTO.getCoupon() != null)
            product.setCoupon(updateProductDTO.getCoupon());
        if ((updateProductDTO.getOriginalPrice() != null
                && !updateProductDTO.getOriginalPrice().equals(product.getOriginalPrice())) ||
                (updateProductDTO.getCoupon() != null && !updateProductDTO.getCoupon().equals(product.getCoupon()))) {
            int price = (int) Math
                    .round(product.getOriginalPrice() - (product.getOriginalPrice() * product.getCoupon() / 100.0));
            product.setPrice(price);
        }
        if (updateProductDTO.getQuantity() != null)
            product.setQuantity(updateProductDTO.getQuantity());
        if (updateProductDTO.getWarranty() != null)
            product.setWarranty(updateProductDTO.getWarranty());
        if (updateProductDTO.getInfor() != null)
            product.setInfor(updateProductDTO.getInfor());
        if (updateProductDTO.getCpu() != null)
            product.setCpu(updateProductDTO.getCpu());
        if (updateProductDTO.getRam() != null)
            product.setRam(updateProductDTO.getRam());
        if (updateProductDTO.getStorage() != null)
            product.setStorage(updateProductDTO.getStorage());
        if (updateProductDTO.getScreen() != null)
            product.setScreen(updateProductDTO.getScreen());
        if (updateProductDTO.getGraphicsCard() != null)
            product.setGraphicsCard(updateProductDTO.getGraphicsCard());
        if (updateProductDTO.getBattery() != null)
            product.setBattery(updateProductDTO.getBattery());
        if (updateProductDTO.getWeight() != null)
            product.setWeight(updateProductDTO.getWeight());
        if (updateProductDTO.getReleaseYear() != null)
            product.setReleaseYear(updateProductDTO.getReleaseYear());
        if (updateProductDTO.getCategory() != null)
            product.setCategory(updateProductDTO.getCategory());
        if (updateProductDTO.getFactory() != null)
            product.setFactory(updateProductDTO.getFactory());

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResProductDTO(updatedProduct);
    }

    public List<ResCardProductDTO> filterProducts(FilterProductDTO filter) {
        StringBuilder sql = new StringBuilder("""
                    SELECT p.id, p.name,
                        p.original_price AS "originalPrice",
                        p.price, p.coupon,
                        COALESCE(ROUND(rv."avgRating",2),0) AS "avgRating",
                        COALESCE(rv."totalReviews",0) AS "totalReviews",
                        img.url AS "imageUrl"
                    FROM products p

                    LEFT JOIN (
                        SELECT
                            product_id,
                            AVG(rating) AS "avgRating",
                            COUNT(*) AS "totalReviews"
                        FROM reviews
                        GROUP BY product_id
                    ) rv ON p.id = rv.product_id

                    LEFT JOIN (
                    SELECT DISTINCT ON (product_id)
                        product_id,
                        url
                    FROM product_images
                    ORDER BY product_id, id
                    ) img ON p.id = img.product_id
                    WHERE p.quantity > 0
                """);

        // Dùng Map để lưu tham số song song với lúc build chuỗi SQL
        Map<String, Object> params = new HashMap<>();
        buildFilters(sql, filter, params);

        Query query = entityManager.createNativeQuery(sql.toString());
        // Gán toàn bộ tham số chỉ bằng 1 dòng
        params.forEach(query::setParameter);
        // XỬ LÝ LỖI MAPPING: Native query trả về List<Object[]>
        @SuppressWarnings("unchecked")
        List<Object[]> rawRows = query.getResultList();
        // Bạn cần viết một hàm mapToDTO để chuyển đổi Object[] thành ResCardProductDTO
        return rawRows.stream().map(row -> new ResCardProductDTO() {
            public Long getId() {
                return ((Number) row[0]).longValue();
            }

            public String getName() {
                return (String) row[1];
            }

            public Integer getOriginalPrice() {
                return row[2] != null ? ((Number) row[2]).intValue() : null;
            }

            public Integer getPrice() {
                return row[3] != null ? ((Number) row[3]).intValue() : null;
            }

            public Integer getCoupon() {
                return row[4] != null ? ((Number) row[4]).intValue() : null;
            }

            public Double getAvgRating() {
                return row[5] != null ? ((Number) row[5]).doubleValue() : null;
            }

            public Long getTotalReviews() {
                return row[6] != null ? ((Number) row[6]).longValue() : null;
            }

            public String getImageUrl() {
                return (String) row[7];
            }
        }).collect(Collectors.toList());
    }

    private void buildFilters(StringBuilder sql, FilterProductDTO filter, Map<String, Object> params) {
        if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
            sql.append(" AND ").append("p.category = :category");
            params.put("category", filter.getCategory());
        }

        addInCondition(sql, params, filter.getFactories(), "p.factory IN (:factories)", "factories");

        addInCondition(sql, params, filter.getProductFeatures(),
                "EXISTS (SELECT 1 FROM product_features pf WHERE pf.product_id = p.id AND pf.feature_id IN (:productFeatures))",
                "productFeatures");

        if (filter.getMinPrice() != null) {
            sql.append(" AND p.price >= :minPrice");
            params.put("minPrice", filter.getMinPrice());
        }

        if (filter.getMaxPrice() != null) {
            sql.append(" AND p.price <= :maxPrice");
            params.put("maxPrice", filter.getMaxPrice());
        }

        addLikeFilter(sql, params, "p.cpu", "cpu", filter.getCpu());

        addLikeFilter(sql, params, "p.ram", "ram", filter.getRam());

        addLikeFilter(sql, params, "p.graphics_card", "gpu", filter.getGpu());

        addLikeFilter(sql, params, "p.storage", "storage", filter.getStorage());

        addLikeFilter(sql, params, "p.screen", "screen", filter.getScreen());

        addLikeFilter(sql, params, "p.screen", "screenSize", filter.getScreenSize());

        handleBatteryFilter(sql, params, filter.getBattery());
    }

    private void addInCondition(StringBuilder sql, Map<String, Object> params,
            List<?> values, String clause, String paramKey) {
        if (values != null && !values.isEmpty()) {
            sql.append(" AND ").append(clause);
            params.put(paramKey, values);
        }
    }

    private void addLikeFilter(StringBuilder sql, Map<String, Object> params,
            String column, String paramPrefix, List<String> values) {
        if (values == null || values.isEmpty() || values.contains("ALL"))
            return;

        sql.append(" AND (");
        for (int i = 0; i < values.size(); i++) {
            String paramKey = paramPrefix + i;
            sql.append("LOWER(").append(column).append(") LIKE LOWER(:").append(paramKey).append(")");
            if (i < values.size() - 1)
                sql.append(" OR ");

            params.put(paramKey, "%" + values.get(i) + "%");
        }
        sql.append(")");
    }

    private void handleBatteryFilter(StringBuilder sql, Map<String, Object> params, List<String> batteries) {
        if (batteries == null || batteries.isEmpty() || batteries.contains("ALL"))
            return;

        sql.append(" AND (");
        for (int i = 0; i < batteries.size(); i++) {
            String minKey = "batteryMin" + i;
            String maxKey = "batteryMax" + i;

            sql.append("(substring(p.battery from '[0-9]+')::int >= :").append(minKey)
                    .append(" AND substring(p.battery from '[0-9]+')::int < :").append(maxKey).append(")");

            if (i < batteries.size() - 1)
                sql.append(" OR ");

            int num = Integer.parseInt(batteries.get(i).replaceAll("\\D", ""));
            params.put(minKey, num);
            params.put(maxKey, num + 1000);
        }
        sql.append(")");
    }
}
