package vn.techzone.khieu.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Schema(description = "Payment response models")
public abstract class ResPaymentDTO {
    @Builder
    @AllArgsConstructor
    @Schema(description = "VNPay payment URL response")
    public static class VNPayResponse {
        @Schema(description = "Payment response code", example = "00")
        public String code;

        @Schema(description = "Payment response message", example = "Success")
        public String message;

        @Schema(description = "Redirect URL for VNPay payment")
        public String paymentUrl;
    }
}
