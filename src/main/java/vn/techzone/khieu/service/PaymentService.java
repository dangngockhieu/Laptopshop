package vn.techzone.khieu.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.config.VNPayConfig;
import vn.techzone.khieu.entity.Order;
import vn.techzone.khieu.entity.Payment;
import vn.techzone.khieu.repository.OrderRepository;
import vn.techzone.khieu.repository.PaymentRepository;
import vn.techzone.khieu.utils.VNPayUtil;
import vn.techzone.khieu.utils.error.FailRequestException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPayConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    // Tạo URL thanh toán cho một order cụ thể
    public String createPaymentUrl(Long orderId, Long userId, String ipAddr) {
        // Kiểm tra order tồn tại và thuộc về user
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new FailRequestException("Order không tồn tại"));

        if (!order.getUser().getId().equals(userId)) {
            throw new FailRequestException("Bạn không có quyền thanh toán đơn hàng này");
        }

        // Kiểm tra payment
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment == null) {
            throw new FailRequestException("Không tìm thấy thông tin thanh toán");
        }
        if ("PAID".equals(payment.getStatus())) {
            throw new FailRequestException("Đơn hàng này đã được thanh toán");
        }

        // Validate amount
        long amount = payment.getAmount();
        if (amount <= 0) {
            throw new FailRequestException("Số tiền không hợp lệ");
        }
        if (amount > 100_000_000L) {
            throw new FailRequestException("Số tiền vượt quá giới hạn 100,000,000 VND");
        }

        // TxnRef = orderId_6-digit-timestamp — tránh duplicate
        String ts = String.valueOf(System.currentTimeMillis());
        String txnRef = orderId + "_" + ts.substring(ts.length() - 6);

        // Build params
        Map<String, String> params = vnPayConfig.getVNPayConfig();
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_IpAddr", ipAddr != null ? ipAddr : "127.0.0.1");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);

        // 6. Ký và build URL
        String queryUrl = VNPayUtil.getPaymentURL(params, true);
        String hashData = VNPayUtil.getPaymentURL(params, false);
        String secureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

        return vnPayConfig.getVnp_PayUrl() + "?" + queryUrl + "&vnp_SecureHash=" + secureHash;
    }

    // Xử lý callback từ VNPay sau khi thanh toán
    @Transactional
    public void handleReturn(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String responseCode = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        String vnpSecureHash = request.getParameter("vnp_SecureHash");

        // Verify chữ ký
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (!k.equals("vnp_SecureHash") && !k.equals("vnp_SecureHashType")) {
                params.put(k, v[0]);
            }
        });

        String hashData = VNPayUtil.getPaymentURL(params, false);
        String checkHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

        if (!checkHash.equalsIgnoreCase(vnpSecureHash)) {
            response.sendRedirect(frontendUrl + "/orders");
            return;
        }

        // Parse orderId từ txnRef = "orderId_xxxxxx"
        Long orderId;
        try {
            orderId = Long.parseLong(txnRef.split("_")[0]);
        } catch (Exception e) {
            response.sendRedirect(frontendUrl + "/orders");
            return;
        }

        // Xử lý kết quả
        if ("00".equals(responseCode)) {
            Payment payment = paymentRepository.findByOrderId(orderId);
            if (payment != null && "UNPAID".equals(payment.getStatus())) {
                payment.setStatus("PAID");
                payment.setTransactionId(transactionNo);
                paymentRepository.save(payment);
            }
            response.sendRedirect(frontendUrl + "/payment-success?orderID=" + orderId);
        } else {
            response.sendRedirect(frontendUrl + "/orders");
        }
    }

    // Kiểm tra trạng thái thanh toán
    public Map<String, Object> checkPaymentStatus(Long orderId, Long userId) {
        Payment payment = paymentRepository.findByOrderId(orderId);

        if (payment == null) {
            throw new FailRequestException("Không tìm thấy thông tin thanh toán");
        }
        if (!payment.getOrder().getUser().getId().equals(userId)) {
            throw new FailRequestException("Bạn không có quyền xem đơn hàng này");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", payment.getStatus());
        result.put("transactionId", payment.getTransactionId());
        result.put("amount", payment.getAmount());
        result.put("orderStatus", payment.getOrder().getStatus());
        return result;
    }
}