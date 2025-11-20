package vn.techzone.khieu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.techzone.khieu.dto.response.cart.ResCartDTO;
import vn.techzone.khieu.entity.Cart;
import vn.techzone.khieu.entity.CartId;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartId> {
    Boolean existsByIdUserIdAndIdProductId(Long userId, Long productId);

    Long countByIdUserId(Long userId);

    @Modifying
    @Query(value = "DELETE FROM carts WHERE user_id = :userId AND product_id IN (:productIds)", nativeQuery = true)
    void deleteByUserIdAndProductIds(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);

    @Query(value = """
            SELECT p.id, p.name, p.price, p.quantity,
                p.original_price AS "originalPrice",
                c.number, c.selected ,
                img.url AS "imageUrl"
            FROM products p
            INNER JOIN carts c ON p.id = c.product_id
            LEFT JOIN (
                SELECT DISTINCT ON (product_id)
                    product_id, url
                FROM product_images
                ORDER BY product_id, id
            ) img ON p.id = img.product_id
            WHERE c.user_id = :userId
            """, nativeQuery = true)
    List<ResCartDTO> getCart(@Param("userId") Long userId);

    List<Cart> findByIdUserId(Long userId);
}
