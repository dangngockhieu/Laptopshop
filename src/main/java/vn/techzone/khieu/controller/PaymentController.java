package vn.techzone.khieu.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.service.PaymentService;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/user/create")
    @ApiMessage("Tạo URL thanh toán thành công")
    public ResponseEntity<Map<String, String>> createPayment(
            @RequestBody Map<String, Long> body,
            HttpServletRequest request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long orderId = body.get("orderId");

        String ipAddr = request.getHeader("X-Forwarded-For");
        if (ipAddr == null)
            ipAddr = request.getRemoteAddr();
        if (ipAddr != null && ipAddr.contains(","))
            ipAddr = ipAddr.split(",")[0].trim();

        String paymentUrl = paymentService.createPaymentUrl(orderId, userId, ipAddr);

        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    @GetMapping("/user/status")
    @ApiMessage("Lấy trạng thái thanh toán thành công")
    public ResponseEntity<?> checkStatus(@RequestParam Long orderID) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(paymentService.checkPaymentStatus(orderID, userId));
    }

    @GetMapping("/return")
    public void vnpayReturn(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        paymentService.handleReturn(request, response);
    }

}