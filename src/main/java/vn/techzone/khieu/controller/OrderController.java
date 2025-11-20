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
import vn.techzone.khieu.dto.request.order.CreateOrderDTO;
import vn.techzone.khieu.dto.request.order.UpdateToShippingDTO;
import vn.techzone.khieu.dto.request.order.UpdateToStatusDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.order.ResOrderCountDTO;
import vn.techzone.khieu.dto.response.order.ResOrderDTO;
import vn.techzone.khieu.dto.response.order.ResOrderItemDTO;
import vn.techzone.khieu.dto.response.order.ResOrderUser.ResOrderUserDTO;
import vn.techzone.khieu.dto.response.order.ResRevenue.ResRevenueThisMonthDTO;
import vn.techzone.khieu.service.OrderService;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping()
    @ApiMessage("Tạo đơn hàng mới")
    public ResponseEntity<Long> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long orderId = orderService.createOrder(userId, createOrderDTO);
        return ResponseEntity.ok(orderId);
    }

    @DeleteMapping()
    @ApiMessage("Hủy đơn hàng đang chờ xử lý")
    public ResponseEntity<Void> cancelOrder(@RequestBody Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetExchange("/pending")
    @ApiMessage("Lấy danh sách đơn hàng đang chờ xử lý")
    public ResponseEntity<PageResponseDTO<ResOrderDTO>> getOrdersPending(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(current - 1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(orderService.getOrdersPending(pageable));
    }

    @GetExchange("/status")
    @ApiMessage("Lấy danh sách đơn hàng theo trạng thái")
    public ResponseEntity<PageResponseDTO<ResOrderDTO>> getOrdersStatus(
            @RequestParam String status,
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(current - 1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(orderService.getOrdersStatus(status, pageable));
    }

    @GetMapping("order-item")
    @ApiMessage("Lấy danh sách sản phẩm trong đơn hàng")
    public ResponseEntity<List<ResOrderItemDTO>> getOrderItems(@RequestParam Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }

    @PatchMapping("/updateToShipping")
    @ApiMessage("Cập nhật trạng thái đơn hàng sang Đang giao")
    public ResponseEntity<Void> updateOrderToShipping(@Valid @RequestBody UpdateToShippingDTO dto) {
        orderService.updatePedingtoShipping(dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updateStatus")
    @ApiMessage("Cập nhật trạng thái đơn hàng")
    public ResponseEntity<Void> updateOrderStatus(@Valid @RequestBody UpdateToStatusDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        orderService.updateStatusOrderforUser(userId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user-orders")
    @ApiMessage("Lấy danh sách đơn hàng của người dùng")
    public ResponseEntity<List<ResOrderUserDTO>> getUserOrders(@RequestParam String status) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(orderService.getUserOrders(userId, status));
    }

    @GetMapping("/count")
    @ApiMessage("Đếm số lượng đơn hàng")
    public ResponseEntity<ResOrderCountDTO> countOrdersByStatus() {
        return ResponseEntity.ok(orderService.countOrders());
    }

    @GetMapping("/revenue")
    @ApiMessage("Thống kê doanh thu theo tháng")
    public ResponseEntity<ResRevenueThisMonthDTO> getRevenueThisMonth() {
        return ResponseEntity.ok(orderService.getRevenueThisMonth());
    }

    @GetMapping("/revenue-by-month")
    @ApiMessage("Thống kê doanh thu theo tháng trong năm")
    public ResponseEntity<List<Long>> getRevenueByMonth() {
        return ResponseEntity.ok(orderService.getRevenueByMonth());
    }
}