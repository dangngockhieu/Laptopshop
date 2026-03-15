package vn.techzone.khieu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.techzone.khieu.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderId(Long orderId);

    @Modifying
    @Query("UPDATE Payment p SET p.status = :status WHERE p.order.id = :orderId")
    void updatePaymentStatus(@Param("status") String status, @Param("orderId") Long orderId);
}
