package vn.techzone.khieu.dto.response.order.ResOrderUser;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface ResUserOrder {
    Long getOrderId();

    Integer getTotalPrice();

    String getStatus();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    Instant getOrderDate();

    String getPaymentMethod();

    String getPaymentStatus();

}
