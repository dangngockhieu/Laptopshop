package vn.techzone.khieu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.techzone.khieu.dto.response.product.ResBestSeller;
import vn.techzone.khieu.dto.response.product.ResCardProductDTO;
import vn.techzone.khieu.dto.response.product.ProductDetailDTO.ResProductDetail;
import vn.techzone.khieu.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsByIdAndQuantityGreaterThan(Long id, Integer quantity);

    Long countLongByQuantityGreaterThan(Integer quantity);

    @Modifying
    @Query(value = "UPDATE products SET quantity = quantity - :qty, sold = sold + :qty WHERE id = :id AND quantity >= :qty", nativeQuery = true)
    int updateQuantityAndSoleCreateOrder(@Param("id") Long id, @Param("qty") Integer qty);

    @Query(value = """
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

                WHERE p.category = :category
                AND p.quantity > 0

                ORDER BY p.sold DESC
                LIMIT 5
            """, nativeQuery = true)
    List<ResCardProductDTO> findAllProductsCategory(@Param("category") String category);

    @Query(value = """
                SELECT p.id, p.name,
                    p.sold
                FROM products p
                WHERE p.quantity > 0
                ORDER BY p.sold DESC
                LIMIT 5
            """, nativeQuery = true)
    List<ResBestSeller> findAllProducts();

    @Query(value = """
            SELECT p.*,
                   ROUND(AVG(r.rating), 2) AS avg_rating,
                   COUNT(r.id)             AS total_reviews
            FROM products p
            LEFT JOIN reviews r ON p.id = r.product_id
            WHERE p.id = :id
            GROUP BY p.id
            """, nativeQuery = true)
    ResProductDetail findProductById(@Param("id") Long id);
}
