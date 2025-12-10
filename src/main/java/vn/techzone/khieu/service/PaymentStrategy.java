package vn.techzone.khieu.service;

public interface PaymentStrategy {
    String createPaymentUrl(Long orderId, Long userId, String ipAddr);
}
