package vn.techzone.khieu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

public abstract class ResPaymentDTO {
    @Builder
    @AllArgsConstructor
    public static class VNPayResponse {
        public String code;
        public String message;
        public String paymentUrl;
    }
}
