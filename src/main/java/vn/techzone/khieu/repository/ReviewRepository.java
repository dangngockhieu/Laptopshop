package vn.techzone.khieu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.techzone.khieu.dto.response.product.ProductDetailDTO.ResReview;
import vn.techzone.khieu.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = """
            SELECT r.id, r.rating, r.comment, r.created_at AS createdAt,
                   u.name AS userName
            FROM reviews r
            INNER JOIN users u ON r.user_id = u.id
            WHERE r.product_id = :productId
            ORDER BY r.created_at DESC
            """, nativeQuery = true)
    List<ResReview> findByProductId(@Param("productId") Long productId);
}
