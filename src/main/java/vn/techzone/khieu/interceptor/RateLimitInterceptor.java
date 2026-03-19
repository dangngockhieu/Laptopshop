package vn.techzone.khieu.interceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import vn.techzone.khieu.utils.annotation.RateLimit;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket(int capacity, int minutes) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, Duration.ofMinutes(minutes))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 1. Kiểm tra xem request này có trỏ tới một hàm trong Controller không
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 2. Tìm Annotation @RateLimit trên hàm đó
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return true; // Nếu hàm không gắn @RateLimit thì cho qua thoải mái
        }

        // 3. Lấy IP người dùng
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null)
            ip = request.getRemoteAddr();

        // 4. Mấu chốt: Tạo Cache Key kết hợp IP và Tên hàm
        // (Để việc giới hạn API Chat không ảnh hưởng đến API Payment)
        String methodName = handlerMethod.getMethod().getName();
        String cacheKey = ip + "-" + methodName;

        // 5. Lấy giá trị động từ Annotation
        int capacity = rateLimit.capacity();
        int minutes = rateLimit.minutes();

        // 6. Áp dụng Bucket4j
        Bucket bucket = cache.computeIfAbsent(cacheKey, k -> createNewBucket(capacity, minutes));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"message\": \"Bạn thao tác quá nhanh, thử lại sau " + waitForRefill + " giây nữa nhé!\"}");
            return false;
        }
    }
}