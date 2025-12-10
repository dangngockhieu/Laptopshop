package vn.techzone.khieu.service.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import vn.techzone.khieu.service.PaymentStrategy;

@Service("COD") // Tên này phải khớp với database
public class CODStrategy implements PaymentStrategy {

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public String createPaymentUrl(Long orderId, Long userId, String ipAddr) {
        return frontendUrl + "/payment-success?orderID=" + orderId;
    }
}