package vn.techzone.khieu.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;

import io.swagger.v3.oas.annotations.tags.Tag;
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
import vn.techzone.khieu.service.RevenueService;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "3. Đơn hàng (Order)", description = "Tạo đơn hàng, cập nhật trạng thái và thống kê doanh thu")
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;
    private final RevenueService revenueService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Tạo đơn hàng mới")
    public ResponseEntity<Long> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long orderId = orderService.createOrder(userId, createOrderDTO);
        return ResponseEntity.ok(orderId);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Hủy đơn hàng đang chờ xử lý")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetExchange("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Lấy danh sách đơn hàng đang chờ xử lý")
    public ResponseEntity<PageResponseDTO<ResOrderDTO>> getOrdersPending(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(current - 1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(orderService.getOrdersPending(pageable));
    }

    @GetExchange("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Lấy danh sách đơn hàng theo trạng thái")
    public ResponseEntity<PageResponseDTO<ResOrderDTO>> getOrdersStatus(
            @RequestParam String status,
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(current - 1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(orderService.getOrdersStatus(status, pageable));
    }

    @GetMapping("/order-item")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Lấy danh sách sản phẩm trong đơn hàng")
    public ResponseEntity<List<ResOrderItemDTO>> getOrderItems(@RequestParam Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }

    @PatchMapping("/updateToShipping/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Cập nhật trạng thái đơn hàng sang Đang giao")
    public ResponseEntity<Void> updateOrderToShipping(@Valid @RequestBody UpdateToShippingDTO dto,
            @PathVariable Long orderId) {
        orderService.updatePedingtoShipping(orderId, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updateToStatus/{orderId}")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Cập nhật trạng thái đơn hàng")
    public ResponseEntity<Void> updateOrderStatus(@Valid @RequestBody UpdateToStatusDTO dto,
            @PathVariable Long orderId) {
        Long userId = SecurityUtil.getCurrentUserId();
        orderService.updateStatusOrderforUser(userId, orderId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user-orders")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Lấy danh sách đơn hàng của người dùng")
    public ResponseEntity<List<ResOrderUserDTO>> getUserOrders(@RequestParam String status) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(orderService.getUserOrders(userId, status));
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Đếm số lượng đơn hàng")
    public ResponseEntity<ResOrderCountDTO> countOrdersByStatus() {
        return ResponseEntity.ok(revenueService.countOrders());
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Thống kê doanh thu theo tháng")
    public ResponseEntity<ResRevenueThisMonthDTO> getRevenueThisMonth() {
        return ResponseEntity.ok(revenueService.getRevenueThisMonth());
    }

    @GetMapping("/revenue-by-month")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Thống kê doanh thu theo tháng trong năm")
    public ResponseEntity<List<Long>> getRevenueByMonth() {
        return ResponseEntity.ok(revenueService.getRevenueByMonth());
    }

    @PostMapping("/buy-again")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Mua lại đơn hàng")
    public ResponseEntity<ResStringDTO> buyAgain(@Valid @RequestBody List<ProductIdDTO> productIdDTOs) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.buyAgain(userId, productIdDTOs.stream().map(ProductIdDTO::getProductId).toList());
        return ResponseEntity.ok(new ResStringDTO("Buy Again Success"));
    }
}