package vn.techzone.khieu.service.payment;

import java.util.Map;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import vn.techzone.khieu.config.VNPayConfig;
import vn.techzone.khieu.entity.Order;
import vn.techzone.khieu.entity.Payment;
import vn.techzone.khieu.repository.OrderRepository;
import vn.techzone.khieu.repository.PaymentRepository;
import vn.techzone.khieu.service.PaymentStrategy;
import vn.techzone.khieu.utils.VNPayUtil;
import vn.techzone.khieu.utils.error.FailRequestException;

@Service("VNPAY") // Tên này phải khớp với database
@RequiredArgsConstructor
public class VNPayStrategy implements PaymentStrategy {

    private final VNPayConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public String createPaymentUrl(Long orderId, Long userId, String ipAddr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new FailRequestException("Order không tồn tại"));

        Payment payment = paymentRepository.findByOrderId(orderId);
        long amount = payment.getAmount();

        String ts = String.valueOf(System.currentTimeMillis());
        String txnRef = orderId + "_" + ts.substring(ts.length() - 6);

        Map<String, String> params = vnPayConfig.getVNPayConfig();
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_IpAddr", ipAddr != null ? ipAddr : "127.0.0.1");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);

        String queryUrl = VNPayUtil.getPaymentURL(params, true);
        String hashData = VNPayUtil.getPaymentURL(params, false);
        String secureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

        return vnPayConfig.getVnp_PayUrl() + "?" + queryUrl + "&vnp_SecureHash=" + secureHash;
    }
}