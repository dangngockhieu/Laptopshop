package vn.techzone.khieu.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.entity.Product;
import vn.techzone.khieu.entity.ProductImage;
import vn.techzone.khieu.repository.ProductImageRepository;
import vn.techzone.khieu.repository.ProductRepository;
import vn.techzone.khieu.utils.error.FailRequestException;
import vn.techzone.khieu.utils.error.StorageException;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FileService fileService;
    private static final String PRODUCT_FOLDER = "products";

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

    public void deleteProductImageByProductId(Long productId) throws StorageException, URISyntaxException {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        if (images.isEmpty()) {
            throw new FailRequestException("Ảnh không tồn tại");
        }

        for (ProductImage image : images) {
            fileService.delete(PRODUCT_FOLDER, image.getUrl().substring(image.getUrl().lastIndexOf("/") + 1));
            productImageRepository.deleteById(image.getId());
        }
    }
}
