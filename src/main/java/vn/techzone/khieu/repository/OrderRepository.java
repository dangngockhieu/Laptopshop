package vn.techzone.khieu.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.techzone.khieu.dto.response.order.ResMonthlyRevenueDTO;
import vn.techzone.khieu.dto.response.order.ResOrderCountDTO;
import vn.techzone.khieu.dto.response.order.ResOrderDTO;
import vn.techzone.khieu.dto.response.order.ResOrderUser.ResUserOrder;
import vn.techzone.khieu.dto.response.order.ResRevenue.ResRevenue;
import vn.techzone.khieu.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Query("DELETE FROM Order o WHERE o.id = :orderId AND o.status = :status")
    void deleteByIdAndStatus(Long orderId, String status);

    @Query(value = """
            SELECT
                o.id AS "orderID", o.user_id AS "userID", o.recipient_name AS "recipientName",
                o.address, o.phone, o.total_price AS "totalPrice",
                o.status AS "orderStatus", o.order_date AS "orderDate",
                o.delivery_date as "deliveryDate",
                o.expected_date as "expectedDate",
                o.received_date as "receivedDate",
                o.tracking_code as "trackingCode",
                p.method AS "paymentMethod", p.status AS "paymentStatus",
                (SELECT u.email FROM users u WHERE u.id = o.user_id) AS "userEmail"
            FROM orders o
            JOIN payments p ON o.id = p.order_id
            WHERE o.status = 'PENDING'
            AND (
                p.status = 'PAID'
                OR (p.status = 'UNPAID' AND p.method = 'COD')
            )
            ORDER BY o.order_date DESC
            """, countQuery = """
            SELECT COUNT(*)
            FROM orders o
            JOIN payments p ON o.id = p.order_id
            WHERE o.status = 'PENDING'
            AND (
                p.status = 'PAID'
                OR (p.status = 'UNPAID' AND p.method = 'COD')
            )
            """, nativeQuery = true)
    Page<ResOrderDTO> getOrdersPending(Pageable pageable);

    @Query(value = """
            SELECT
                o.id AS "orderID", o.user_id AS "userID", o.recipient_name AS "recipientName",
                o.address, o.phone, o.total_price AS "totalPrice",
                o.status AS "orderStatus", o.order_date AS "orderDate",
                o.delivery_date as "deliveryDate",
                o.expected_date as "expectedDate",
                o.received_date as "receivedDate",
                o.tracking_code as "trackingCode",
                p.method AS "paymentMethod", p.status AS "paymentStatus",
                (SELECT u.email FROM users u WHERE u.id = o.user_id) AS "userEmail"
            FROM orders o
            JOIN payments p ON o.id = p.order_id
            WHERE o.status = :status
            ORDER BY o.order_date DESC
            """, countQuery = """
            SELECT COUNT(*)
            FROM orders o
            JOIN payments p ON o.id = p.order_id
            WHERE o.status = :status
            """, nativeQuery = true)
    Page<ResOrderDTO> getOrdersStatus(Pageable pageable, @Param("status") String status);

    @Query(value = """
            SELECT o.id AS "orderId", o.total_price AS "totalPrice", o.status,
                o.order_date AS "orderDate",
                pm.method AS "paymentMethod", pm.status AS "paymentStatus"
            FROM orders o
            JOIN payments pm ON pm.order_id = o.id
            WHERE o.user_id = :userId AND o.status = :status
            ORDER BY o.order_date DESC
            """, nativeQuery = true)
    List<ResUserOrder> getUserOrders(@Param("userId") Long userId, @Param("status") String status);

    @Query(value = """
            SELECT
                COUNT(*) FILTER (WHERE status <> 'CANCELLED') AS "count",
                COUNT(*) FILTER (WHERE status = 'PENDING') AS "countPending",
                COUNT(*) FILTER (WHERE status = 'SHIPPING') AS "countShipping",
                COUNT(*) FILTER (WHERE status = 'COMPLETED') AS "countCompleted"
            FROM orders
            WHERE order_date BETWEEN :start AND :end
            """, nativeQuery = true)
    ResOrderCountDTO countOrders(@Param("start") Instant start, @Param("end") Instant end);

    @Query(value = """
            SELECT
                SUM(CASE WHEN order_date BETWEEN :startCurrent AND :endCurrent THEN total_price ELSE 0 END) AS "currentMonthRevenue",
                SUM(CASE WHEN order_date BETWEEN :startPrev AND :endPrev THEN total_price ELSE 0 END) AS "prevMonthRevenue"
            FROM orders
            WHERE status = 'COMPLETED'
            AND order_date BETWEEN :startPrev AND :endCurrent
            """, nativeQuery = true)
    ResRevenue getRevenue(
            @Param("startCurrent") Instant startCurrent,
            @Param("endCurrent") Instant endCurrent,
            @Param("startPrev") Instant startPrev,
            @Param("endPrev") Instant endPrev);

    @Query(value = """
            SELECT
                EXTRACT(MONTH FROM order_date AT TIME ZONE 'Asia/Ho_Chi_Minh') AS month,
                SUM(total_price) AS revenue
            FROM orders
            WHERE status = 'COMPLETED'
            AND order_date BETWEEN :start AND :end
            GROUP BY month
            ORDER BY month
            """, nativeQuery = true)
    List<ResMonthlyRevenueDTO> getRevenueByMonth(@Param("start") Instant start, @Param("end") Instant end);
}
