package vn.techzone.khieu.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.product.CreateProductDTO;
import vn.techzone.khieu.dto.request.product.ProductFeatureDTO;
import vn.techzone.khieu.dto.request.product.UpdateProductDTO;
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
import vn.techzone.khieu.repository.ProductImageRepository;
import vn.techzone.khieu.repository.ProductRepository;
import vn.techzone.khieu.repository.ReviewRepository;
import vn.techzone.khieu.utils.error.FailRequestException;
import vn.techzone.khieu.utils.error.NotFindException;
import vn.techzone.khieu.utils.error.StorageException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FileService fileService;
    private final ReviewRepository reviewRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageService productImageService;
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

    @Transactional(readOnly = true)
    public PageResponseDTO<ResProductDTO> getAllProducts(Pageable pageable, Specification<Product> spec) {
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
            productImageService.deleteProductImageByProductId(id);
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

    public FilterProductResponseDTO filterProducts(Specification<Product> spec) {
        Specification<Product> quantitySpec = (root, query, cb) -> cb.greaterThan(root.get("quantity"), 0);
        List<Product> productList = productRepository.findAll(spec.and(quantitySpec),
                Sort.by(Sort.Direction.DESC, "sold"));

        List<ResCardProductDTO> products = productList.stream()
                .map(this::toResCardProductDTO)
                .collect(Collectors.toList());

        return new FilterProductResponseDTO(products, products.size());
    }

    private ResCardProductDTO toResCardProductDTO(Product product) {
        List<Integer> ratings = product.getReviews().stream()
                .map(Review::getRating)
                .collect(Collectors.toList());
        double avgRating = ratings.isEmpty() ? 0.0
                : ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        String imageUrl = product.getImages().stream()
                .findFirst()
                .map(ProductImage::getUrl)
                .orElse(null);

        return new ResCardProductDTO() {
            public Long getId() {
                return product.getId();
            }

            public String getName() {
                return product.getName();
            }

            public Integer getOriginalPrice() {
                return product.getOriginalPrice();
            }

            public Integer getPrice() {
                return product.getPrice();
            }

            public Integer getCoupon() {
                return product.getCoupon();
            }

            public Double getAvgRating() {
                return Math.round(avgRating * 100.0) / 100.0;
            }

            public Long getTotalReviews() {
                return (long) ratings.size();
            }

            public String getImageUrl() {
                return imageUrl;
            }
        };
    }
}