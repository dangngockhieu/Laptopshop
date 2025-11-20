package vn.techzone.khieu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.techzone.khieu.dto.response.order.ResOrderItemDTO;
import vn.techzone.khieu.dto.response.order.ResOrderUser.ResOrderItemUser;
import vn.techzone.khieu.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query(value = """
            SELECT oi.id
            FROM "order_items" oi
            JOIN "orders" o ON oi."orderID" = o.id
            WHERE oi.id = :orderItemId
                AND o."userID" = :userId
                AND o.status = 'COMPLETED'
                AND oi."reviewed" = false
            LIMIT 1
            """, nativeQuery = true)
    Long getOrderItemNotReview(@Param("orderItemId") Long orderItemId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM OrderItem o WHERE o.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);

    @Modifying
    @Query(value = """
            UPDATE products p
            SET quantity = p.quantity + oi.quantity,
                sold = p.sold - oi.quantity
            FROM order_items oi
            WHERE p.id = oi.product_id AND oi.order_id = :orderId
            """, nativeQuery = true)
    void restoreQuantityByOrderId(@Param("orderId") Long orderId);

    @Query(value = """
            SELECT
                oi.product_id AS "productId", p.name, oi.quantity, oi.price AS "unitPrice",
                img.url AS "imageUrl"
            FROM order_items oi
            JOIN products p ON oi.product_id = p.id
            LEFT JOIN (
                SELECT DISTINCT ON (product_id)
                    product_id, url
                FROM product_images
                ORDER BY product_id, id
            ) img ON p.id = img.product_id
            WHERE oi.order_id = :orderId
            """, nativeQuery = true)
    List<ResOrderItemDTO> findByOrderId(@Param("orderId") Long orderId);

    @Query(value = """
            SELECT oi.order_id AS "orderId", p.id AS "productId", p.name AS "productName",
                oi.id AS "orderItemId", oi.quantity, oi.price AS "unitPrice", oi.reviewed AS "reviewed",
                img.url AS "imageUrl"
            FROM order_items oi
            JOIN products p ON p.id = oi.product_id
            LEFT JOIN (
                SELECT DISTINCT ON (product_id) product_id, url
                FROM product_images
                ORDER BY product_id, id
            ) img ON p.id = img.product_id
            WHERE oi.order_id IN (:orderIds)
            """, nativeQuery = true)
    List<ResOrderItemUser> getOrderItems(@Param("orderIds") List<Long> orderIds);
}
