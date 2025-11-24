package vn.techzone.khieu.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.product.CreateProductDTO;
import vn.techzone.khieu.dto.request.product.FilterProductDTO;
import vn.techzone.khieu.dto.request.product.ProductFeatureDTO;
import vn.techzone.khieu.dto.request.product.UpdateProductDTO;
import vn.techzone.khieu.dto.request.review.CreateReviewDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.product.ResProductDTO;
import vn.techzone.khieu.dto.response.product.AllProductForChatBot.ResProductforAiChatBotDTO;
import vn.techzone.khieu.dto.response.product.AllProductForChatBot.ResProductforAiChatBotProjection;
import vn.techzone.khieu.dto.response.product.ProductDetailDTO.ResProductDetail;
import vn.techzone.khieu.dto.response.product.ProductDetailDTO.ResProductDetailDTO;
import vn.techzone.khieu.dto.response.product.ProductDetailDTO.ResReview;
import vn.techzone.khieu.dto.response.product.ProductDetailDTO.ResReviewSummary;
import vn.techzone.khieu.dto.response.product.FilterProductResponseDTO;
import vn.techzone.khieu.dto.response.product.ResBestSeller;
import vn.techzone.khieu.dto.response.product.ResCardProductDTO;
import vn.techzone.khieu.entity.Product;
import vn.techzone.khieu.entity.ProductImage;
import vn.techzone.khieu.entity.Review;
import vn.techzone.khieu.mapper.ProductMapper;
import vn.techzone.khieu.repository.OrderItemRepository;
import vn.techzone.khieu.repository.ProductImageRepository;
import vn.techzone.khieu.repository.ProductRepository;
import vn.techzone.khieu.repository.ReviewRepository;
import vn.techzone.khieu.repository.UserRepository;
import vn.techzone.khieu.utils.GenericSpecification;
import vn.techzone.khieu.utils.error.FailRequestException;
import vn.techzone.khieu.utils.error.NotFindException;
import vn.techzone.khieu.utils.error.StorageException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final EntityManager entityManager;
    private final FileService fileService;
    private final ReviewRepository reviewRepository;
    private final ProductImageRepository productImageRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private static final String PRODUCT_FOLDER = "products";

    @Transactional
    public Product createProduct(CreateProductDTO dto, List<MultipartFile> images)
            throws URISyntaxException, IOException, StorageException {
        if (dto.getCoupon() == null) {
            dto.setCoupon(0);
        }
        Integer price = (int) Math.round(dto.getOriginalPrice() - (dto.getOriginalPrice() * dto.getCoupon() / 100.0));
        Product product = productMapper.toCreateProduct(dto);
        product.setPrice(price);
        product.setSold(0);
        this.productRepository.save(product);

        List<String> uploadedUrls = new ArrayList<>();
        try {
            if (images != null && !images.isEmpty()) {
                fileService.createDirectory(PRODUCT_FOLDER);
                for (MultipartFile image : images) {
                    String uploadedFileName = fileService.store(image, PRODUCT_FOLDER);
                    String url = "/storage/" + PRODUCT_FOLDER + "/" + uploadedFileName;
                    uploadedUrls.add(url);

                    ProductImage productImage = new ProductImage();
                    productImage.setUrl(url);
                    productImage.setProduct(product);
                    productImageRepository.save(productImage);
                }
            }
        } catch (Exception e) {
            uploadedUrls.forEach(url -> {
                try {
                    fileService.delete(PRODUCT_FOLDER, url.substring(url.lastIndexOf("/") + 1));
                } catch (StorageException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            });
            throw e;
        }

        return product;
    }

    @Transactional
    public void addProductImages(Long productId, List<MultipartFile> images)
            throws URISyntaxException, IOException, StorageException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new FailRequestException("Sản phẩm không tồn tại"));

        List<String> uploadedUrls = new ArrayList<>();
        try {
            if (images != null && !images.isEmpty()) {
                fileService.createDirectory(PRODUCT_FOLDER);
                for (MultipartFile image : images) {
                    String uploadedFileName = fileService.store(image, PRODUCT_FOLDER);
                    String url = "/storage/" + PRODUCT_FOLDER + "/" + uploadedFileName;
                    uploadedUrls.add(url);

                    ProductImage productImage = new ProductImage();
                    productImage.setUrl(url);
                    productImage.setProduct(product);
                    productImageRepository.save(productImage);
                }
            }
        } catch (Exception e) {
            uploadedUrls.forEach(url -> {
                try {
                    fileService.delete(PRODUCT_FOLDER, url.substring(url.lastIndexOf("/") + 1));
                } catch (StorageException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            });
            throw e;
        }
    }

    public void deleteProductImage(Long imageId) throws StorageException, URISyntaxException {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new FailRequestException("Ảnh không tồn tại"));

        fileService.delete(PRODUCT_FOLDER, image.getUrl().substring(image.getUrl().lastIndexOf("/") + 1));
        productImageRepository.deleteById(imageId);
    }

    private void deleteProductImageByProductId(Long productId) throws StorageException, URISyntaxException {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        if (images.isEmpty()) {
            throw new FailRequestException("Ảnh không tồn tại");
        }

        for (ProductImage image : images) {
            fileService.delete(PRODUCT_FOLDER, image.getUrl().substring(image.getUrl().lastIndexOf("/") + 1));
            productImageRepository.deleteById(image.getId());
        }
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<ResProductDTO> getAllProducts(Pageable pageable, String keyword, String category,
            String factory) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required");
        }

        // Build spec
        Specification<Product> spec = GenericSpecification.equal("category", category);

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(GenericSpecification.like("name", keyword));
        }

        if (factory != null && !factory.isBlank() && !factory.equalsIgnoreCase("ALL")) {
            spec = spec.and(GenericSpecification.equal("factory", factory));
        }

        // Bước 1: pagination đúng trong SQL
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<Long> ids = productPage.getContent()
                .stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return new PageResponseDTO<>(
                    Collections.emptyList(),
                    0L, 0, 1, pageable.getPageSize());
        }

        Map<Long, Product> productMap = productRepository.findAllWithDetailsByIds(ids)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<ResProductDTO> products = ids.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .map(productMapper::toResProductDTO)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                products,
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.getNumber() + 1,
                productPage.getSize());
    }

    public List<ResCardProductDTO> getTopProductsCategory(String category) {
        return productRepository.findAllProductsCategory(category);
    }

    public List<ResBestSeller> getTopProducts() {
        return productRepository.findAllProducts();
    }

    public ResProductDTO updateProduct(Long id, UpdateProductDTO updateProductDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (updateProductDTO.getName() != null)
            product.setName(updateProductDTO.getName());
        if ((updateProductDTO.getOriginalPrice() != null
                && !updateProductDTO.getOriginalPrice().equals(product.getOriginalPrice())) ||
                (updateProductDTO.getCoupon() != null && !updateProductDTO.getCoupon().equals(product.getCoupon()))) {
            int price = (int) Math
                    .round(updateProductDTO.getOriginalPrice()
                            - (updateProductDTO.getOriginalPrice() * updateProductDTO.getCoupon() / 100.0));
            product.setPrice(price);
        }
        if (updateProductDTO.getOriginalPrice() != null)
            product.setOriginalPrice(updateProductDTO.getOriginalPrice());
        if (updateProductDTO.getCoupon() != null)
            product.setCoupon(updateProductDTO.getCoupon());
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

    public ResProductDetailDTO getProductById(Long id) {
        ResProductDetail product = productRepository.findProductById(id);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        List<String> imageUrls = productImageRepository.findUrlsByProductId(id);
        List<ResReview> reviewItems = reviewRepository.findByProductId(id);

        ResReviewSummary reviewSummary = new ResReviewSummary();
        reviewSummary.setAvgRating(product.getAvgRating() != null ? product.getAvgRating() : 0.0);
        reviewSummary.setTotalReviews(product.getTotalReviews() != null ? product.getTotalReviews() : 0L);
        reviewSummary.setItems(reviewItems);

        ResProductDetailDTO dto = new ResProductDetailDTO();
        dto.setProduct(product);
        dto.setImageUrls(imageUrls != null ? imageUrls : new ArrayList<>());
        dto.setReviews(reviewSummary);

        return dto;
    }

    public void createReview(Long userId, CreateReviewDTO createReviewDTO) {
        Long alreadyReviewed = orderItemRepository.getOrderItemNotReview(createReviewDTO.getOrderItemId(), userId);
        if (alreadyReviewed == null) {
            throw new BadCredentialsException(
                    "You have already reviewed this product or you are not eligible to review");
        }
        Product product = productRepository.findById(createReviewDTO.getProductId())
                .orElseThrow(
                        () -> new NotFindException("Product not found with id: " + createReviewDTO.getProductId()));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(userRepository.getReferenceById(userId));
        review.setRating(createReviewDTO.getRating());
        review.setComment(createReviewDTO.getComment());

        reviewRepository.save(review);
    }

    public List<ResProductforAiChatBotDTO> getAllProductsForChatBot() {
        List<ResProductforAiChatBotProjection> projections = productRepository.findAllProductsforChatBot();
        return projections.stream()
                .map(p -> new ResProductforAiChatBotDTO(
                        p.getId(),
                        p.getName(),
                        p.getOriginalPrice(),
                        p.getPrice(),
                        p.getCoupon(),
                        p.getQuantity(),
                        p.getSold(),
                        p.getWarranty(),
                        p.getInfor(),
                        p.getCpu(),
                        p.getRam(),
                        p.getStorage(),
                        p.getScreen(),
                        p.getGraphicsCard(),
                        p.getBattery(),
                        p.getWeight(),
                        p.getReleaseYear(),
                        p.getCategory(),
                        p.getFactory(),
                        p.getImageUrl(),
                        p.getFeatures() != null ? Arrays.asList(p.getFeatures().split(",")) : List.of()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFindException("Product not found with id: " + id));
        if (product.getSold() != null && product.getSold() > 0) {
            throw new FailRequestException("Cannot delete product that has been sold");
        }
        productRepository.deleteFeature(id);
        try {
            deleteProductImageByProductId(id);
        } catch (StorageException | URISyntaxException e) {
            throw new RuntimeException("Failed to delete product images", e);
        }
        productRepository.deleteById(id);
    }

    public Long countProducts() {
        return productRepository.countLongByQuantityGreaterThan(0);
    }

    @Transactional
    public void addFeaturesToProduct(Long productId, List<ProductFeatureDTO> featureDTOs) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NotFindException("Product not found with id: " + productId));
        for (ProductFeatureDTO featureDTO : featureDTOs) {
            productRepository.addFeatures(productId, featureDTO.getFeatureId());
        }
    }

    @Transactional
    public void deleteFeatureFromProduct(Long productId, Long featureId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NotFindException("Product not found with id: " + productId));
        productRepository.deleteFeatures(productId, featureId);
    }

    public FilterProductResponseDTO filterProducts(FilterProductDTO filter) {

        // ===== PRODUCTS QUERY =====
        StringBuilder sql = new StringBuilder("""
                SELECT p.id, p.name,
                    p.original_price AS originalPrice,
                    p.price, p.coupon,
                    COALESCE(ROUND(CAST(rv.avgRating AS numeric), 2), 0) AS avgRating,
                    COALESCE(rv.totalReviews, 0) AS totalReviews,
                    img.url AS imageUrl
                FROM products p

                LEFT JOIN (
                    SELECT product_id,
                           AVG(rating) AS avgRating,
                           COUNT(*) AS totalReviews
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

        Map<String, Object> params = new HashMap<>();
        buildFilters(sql, filter, params);
        sql.append(" ORDER BY p.sold DESC");

        Query query = entityManager.createNativeQuery(sql.toString());
        params.forEach(query::setParameter);

        @SuppressWarnings("unchecked")
        List<Object[]> rawRows = query.getResultList();

        List<ResCardProductDTO> products = rawRows.stream().map(row -> new ResCardProductDTO() {
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

        // ===== COUNT QUERY =====
        StringBuilder countSql = new StringBuilder("""
                SELECT COUNT(DISTINCT p.id)
                FROM products p
                WHERE p.quantity > 0
                """);

        Map<String, Object> countParams = new HashMap<>();
        buildFilters(countSql, filter, countParams);

        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        countParams.forEach(countQuery::setParameter);
        Object countResult = countQuery.getSingleResult();
        long count = countResult instanceof Number ? ((Number) countResult).longValue() : 0L;

        return new FilterProductResponseDTO(products, count);
    }

    private void buildFilters(StringBuilder sql, FilterProductDTO filter, Map<String, Object> params) {
        if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
            sql.append(" AND p.category = :category");
            params.put("category", filter.getCategory());
        }

        addInCondition(sql, params, filter.getFactories(),
                "p.factory IN (:factories)", "factories");

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