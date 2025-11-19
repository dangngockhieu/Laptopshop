package vn.techzone.khieu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.techzone.khieu.dto.response.product.ResCardProductDTO;
import vn.techzone.khieu.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsByIdAndQuantityGreaterThan(Long id, Integer quantity);

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

                WHERE (:category IS NULL
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%', :category, '%')))
                AND p.quantity > 0

                ORDER BY p.sold DESC
                LIMIT 5
            """, nativeQuery = true)
    List<ResCardProductDTO> findAllProducts(@Param("category") String category);
}
