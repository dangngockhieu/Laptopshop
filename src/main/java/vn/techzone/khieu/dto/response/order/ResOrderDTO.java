package vn.techzone.khieu.dto.response.order;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface ResOrderDTO {
    Long getOrderId();

    Long getUserId();

    String getRecipientName();

    String getAdderss();

    String getPhone();

    Integer getTotalPrice();

    String getOrderStatus();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    Instant getOrderDate();

    String getPaymentMethod();

    String getPaymentStatus();

    String getUserEmail();
}
