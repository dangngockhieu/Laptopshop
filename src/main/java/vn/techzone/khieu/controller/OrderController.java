package vn.techzone.khieu.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.ProductIdDTO;
import vn.techzone.khieu.dto.request.order.CreateOrderDTO;
import vn.techzone.khieu.dto.request.order.UpdateToShippingDTO;
import vn.techzone.khieu.dto.request.order.UpdateToStatusDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.order.ResOrderCountDTO;
import vn.techzone.khieu.dto.response.order.ResOrderDTO;
import vn.techzone.khieu.dto.response.order.ResOrderItemDTO;
import vn.techzone.khieu.dto.response.order.ResOrderUser.ResOrderUserDTO;
import vn.techzone.khieu.dto.response.order.ResRevenue.ResRevenueThisMonthDTO;
import vn.techzone.khieu.dto.response.user.ResStringDTO;
import vn.techzone.khieu.service.CartService;
import vn.techzone.khieu.service.OrderService;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;

    @PostMapping("/user")
    @ApiMessage("Tạo đơn hàng mới")
    public ResponseEntity<Long> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long orderId = orderService.createOrder(userId, createOrderDTO);
        return ResponseEntity.ok(orderId);
    }

    @DeleteMapping("/admin/{orderId}")
    @ApiMessage("Hủy đơn hàng đang chờ xử lý")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetExchange("/admin/pending")
    @ApiMessage("Lấy danh sách đơn hàng đang chờ xử lý")
    public ResponseEntity<PageResponseDTO<ResOrderDTO>> getOrdersPending(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(current - 1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(orderService.getOrdersPending(pageable));
    }

    @GetExchange("/admin/status")
    @ApiMessage("Lấy danh sách đơn hàng theo trạng thái")
    public ResponseEntity<PageResponseDTO<ResOrderDTO>> getOrdersStatus(
            @RequestParam String status,
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(current - 1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(orderService.getOrdersStatus(status, pageable));
    }

    @GetMapping("/admin/order-item")
    @ApiMessage("Lấy danh sách sản phẩm trong đơn hàng")
    public ResponseEntity<List<ResOrderItemDTO>> getOrderItems(@RequestParam Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }

    @PatchMapping("/admin/updateToShipping/{orderId}")
    @ApiMessage("Cập nhật trạng thái đơn hàng sang Đang giao")
    public ResponseEntity<Void> updateOrderToShipping(@Valid @RequestBody UpdateToShippingDTO dto,
            @PathVariable Long orderId) {
        orderService.updatePedingtoShipping(orderId, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/user/updateToStatus/{orderId}")
    @ApiMessage("Cập nhật trạng thái đơn hàng")
    public ResponseEntity<Void> updateOrderStatus(@Valid @RequestBody UpdateToStatusDTO dto,
            @PathVariable Long orderId) {
        Long userId = SecurityUtil.getCurrentUserId();
        orderService.updateStatusOrderforUser(userId, orderId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/user-orders")
    @ApiMessage("Lấy danh sách đơn hàng của người dùng")
    public ResponseEntity<List<ResOrderUserDTO>> getUserOrders(@RequestParam String status) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(orderService.getUserOrders(userId, status));
    }

    @GetMapping("/admin/count")
    @ApiMessage("Đếm số lượng đơn hàng")
    public ResponseEntity<ResOrderCountDTO> countOrdersByStatus() {
        return ResponseEntity.ok(orderService.countOrders());
    }

    @GetMapping("/admin/revenue")
    @ApiMessage("Thống kê doanh thu theo tháng")
    public ResponseEntity<ResRevenueThisMonthDTO> getRevenueThisMonth() {
        return ResponseEntity.ok(orderService.getRevenueThisMonth());
    }

    @GetMapping("/admin/revenue-by-month")
    @ApiMessage("Thống kê doanh thu theo tháng trong năm")
    public ResponseEntity<List<Long>> getRevenueByMonth() {
        return ResponseEntity.ok(orderService.getRevenueByMonth());
    }

    @PostMapping("/user/buy-again")
    @ApiMessage("Mua lại đơn hàng")
    public ResponseEntity<ResStringDTO> buyAgain(@Valid @RequestBody List<ProductIdDTO> productIdDTOs) {
        Long userId = SecurityUtil.getCurrentUserId();
        for (ProductIdDTO dto : productIdDTOs) {
            try {
                cartService.buyNow(userId, dto.getProductId());
            } catch (Exception e) {
                // Bỏ qua sản phẩm lỗi, tiếp tục thêm cái khác
            }
        }
        return ResponseEntity.ok(new ResStringDTO("Buy Again Success"));
    }
}