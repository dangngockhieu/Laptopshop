package vn.techzone.khieu.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.config.VNPayConfig;
import vn.techzone.khieu.entity.Payment;
import vn.techzone.khieu.repository.PaymentRepository;
import vn.techzone.khieu.service.PaymentStrategy;
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

    private final Map<String, PaymentStrategy> paymentStrategies;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    // Tự động điều hướng tạo URL
    public String createPaymentUrl(Long orderId, Long userId, String ipAddr) {
        Payment payment = paymentRepository.findByOrderId(orderId);

        if (payment == null) {
            throw new FailRequestException("Không tìm thấy thông tin thanh toán");
        }
        if (!payment.getOrder().getUser().getId().equals(userId)) {
            throw new FailRequestException("Bạn không có quyền thanh toán đơn hàng này");
        }
        if ("PAID".equals(payment.getStatus())) {
            throw new FailRequestException("Đơn hàng này đã được thanh toán");
        }

        // Lấy phương thức (VNPAY hoặc COD)
        String method = payment.getMethod().toUpperCase();

        PaymentStrategy strategy = paymentStrategies.get(method);
        if (strategy == null) {
            throw new FailRequestException("Phương thức thanh toán " + method + " chưa được hỗ trợ");
        }

        return strategy.createPaymentUrl(orderId, userId, ipAddr);
    }

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