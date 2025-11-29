package vn.techzone.khieu.service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.order.CreateOrderDTO;
import vn.techzone.khieu.dto.request.order.UpdateToShippingDTO;
import vn.techzone.khieu.dto.request.order.UpdateToStatusDTO;
import vn.techzone.khieu.dto.request.order.CreateOrderDTO.OrderItemDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.order.ResOrderDTO;
import vn.techzone.khieu.dto.response.order.ResOrderItemDTO;
import vn.techzone.khieu.dto.response.order.ResOrderUser.ResOrderItemUser;
import vn.techzone.khieu.dto.response.order.ResOrderUser.ResOrderUserDTO;
import vn.techzone.khieu.dto.response.order.ResOrderUser.ResUserOrder;
import vn.techzone.khieu.entity.Order;
import vn.techzone.khieu.entity.OrderItem;
import vn.techzone.khieu.entity.Payment;
import vn.techzone.khieu.repository.CartRepository;
import vn.techzone.khieu.repository.OrderItemRepository;
import vn.techzone.khieu.repository.OrderRepository;
import vn.techzone.khieu.repository.PaymentRepository;
import vn.techzone.khieu.repository.ProductRepository;
import vn.techzone.khieu.repository.UserRepository;
import vn.techzone.khieu.utils.error.FailRequestException;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Long createOrder(Long userId, CreateOrderDTO dto) {
        // TRỪ KHO & KIỂM TRA TỒN KHO
        for (OrderItemDTO item : dto.getItems()) {
            int updated = productRepository.updateQuantityAndSoleCreateOrder(item.getProductId(), item.getQuantity());
            if (updated == 0) {
                throw new FailRequestException("Sản phẩm ID " + item.getProductId() + " không đủ hàng hoặc đã hết!");
            }
        }

        // TẠO ORDER
        Order order = new Order();
        order.setUser(userRepository.getReferenceById(userId));
        order.setRecipientName(dto.getRecipientName());
        order.setAddress(dto.getAddress());
        order.setPhone(dto.getPhone());
        order.setTotalPrice(dto.getTotalPrice());
        order.setStatus("PENDING");
        orderRepository.save(order);

        // TẠO ORDER ITEMS
        for (OrderItemDTO item : dto.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(productRepository.getReferenceById(item.getProductId()));
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());
            orderItemRepository.save(orderItem);
        }

        // TẠO PAYMENT
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(dto.getTotalPrice());
        payment.setMethod(dto.getPaymentMethod());
        payment.setStatus("UNPAID");
        paymentRepository.save(payment);

        // XÓA CART
        List<Long> productIds = Arrays.stream(dto.getItems())
                .map(OrderItemDTO::getProductId)
                .collect(Collectors.toList());
        cartRepository.deleteByUserIdAndProductIds(userId, productIds);

        return order.getId();
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new FailRequestException("Đơn hàng không tồn tại"));

        if (!order.getStatus().equals("PENDING")) {
            throw new FailRequestException("Chỉ có thể xóa đơn hàng đang chờ xử lý");
        }

        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment == null || payment.getStatus().equals("PAID")) {
            throw new FailRequestException("Không thể xóa đơn hàng đã thanh toán");
        }

        entityManager.createNativeQuery("DELETE FROM payments WHERE order_id = :orderId")
                .setParameter("orderId", orderId)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM order_items WHERE order_id = :orderId")
                .setParameter("orderId", orderId)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM orders WHERE id = :orderId AND status = 'PENDING'")
                .setParameter("orderId", orderId)
                .executeUpdate();
    }

    public PageResponseDTO<ResOrderDTO> getOrdersPending(Pageable pageable) {
        Page<ResOrderDTO> orderPage = orderRepository.getOrdersPending(pageable);
        List<ResOrderDTO> orders = orderPage.getContent().stream()
                .collect(Collectors.toList());
        return new PageResponseDTO<ResOrderDTO>(
                orders,
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.getNumber() + 1,
                orderPage.getSize());
    }

    public PageResponseDTO<ResOrderDTO> getOrdersStatus(String status, Pageable pageable) {
        if (!List.of("PENDING", "SHIPPING", "COMPLETED", "CANCELED").contains(status)) {
            throw new FailRequestException("Trạng thái không hợp lệ");
        }
        Page<ResOrderDTO> orderPage = orderRepository.getOrdersStatus(pageable, status);
        List<ResOrderDTO> orders = orderPage.getContent().stream()
                .collect(Collectors.toList());
        return new PageResponseDTO<ResOrderDTO>(
                orders,
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.getNumber() + 1,
                orderPage.getSize());
    }

    public List<ResOrderItemDTO> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public void updatePedingtoShipping(Long orderId, UpdateToShippingDTO updateToShippingDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new FailRequestException("Đơn hàng không tồn tại"));

        if (!order.getStatus().equals("PENDING")) {
            throw new FailRequestException("Chỉ có thể cập nhật đơn hàng đang chờ xử lý");
        }

        order.setStatus("SHIPPING");
        order.setTrackingCode(updateToShippingDTO.getTrackingCode());
        order.setExpectedDate(updateToShippingDTO.getExpectedDate());
        order.setDeliveryDate(Instant.now());
        orderRepository.save(order);
    }

    @Transactional
    public void updateStatusOrderforUser(Long userId, Long orderId, UpdateToStatusDTO updateToStatusDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new FailRequestException("Đơn hàng không tồn tại"));

        if (!order.getUser().getId().equals(userId)) {
            throw new FailRequestException("Không có quyền cập nhật đơn hàng này");
        }

        List<String> allowedStatuses = List.of("CANCELED", "COMPLETED");
        if (!allowedStatuses.contains(updateToStatusDTO.getStatus())) {
            throw new FailRequestException(
                    "Trạng thái không hợp lệ. Chỉ có thể cập nhật thành CANCELED hoặc COMPLETED");
        }

        Payment payment = paymentRepository.findByOrderId(order.getId());

        if (updateToStatusDTO.getStatus().equals("COMPLETED")) {
            if (!order.getStatus().equals("SHIPPING")) {
                throw new FailRequestException("Chỉ có thể xác nhận nhận hàng khi đơn đang vận chuyển");
            }
            order.setReceivedDate(Instant.now());
            order.setStatus("COMPLETED");
            orderRepository.save(order);
            if (payment != null && payment.getStatus().equals("UNPAID") && payment.getMethod().equals("COD")) {
                payment.setStatus("PAID");
                paymentRepository.save(payment);
            }
        } else {
            if (!order.getStatus().equals("PENDING") || payment.getStatus().equals("PAID")) {
                throw new FailRequestException("Chỉ có thể hủy đơn hàng khi đang chờ xử lý và chưa thanh toán");
            }
            order.setStatus("CANCELED");
            orderRepository.save(order);
            // HOÀN TRẢ KHO
            orderItemRepository.restoreQuantityByOrderId(order.getId());
        }
    }

    public List<ResOrderUserDTO> getUserOrders(Long userId, String status) {
        if (!List.of("PENDING", "SHIPPING", "COMPLETED", "CANCELED").contains(status)) {
            throw new FailRequestException("Trạng thái không hợp lệ");
        }
        List<ResUserOrder> orders = orderRepository.getUserOrders(userId, status);

        List<Long> orderIds = orders.stream()
                .map(ResUserOrder::getOrderId)
                .collect(Collectors.toList());

        List<ResOrderItemUser> items = orderItemRepository.getOrderItems(orderIds);

        Map<Long, List<ResOrderItemUser>> itemsByOrderId = items.stream()
                .collect(Collectors.groupingBy(ResOrderItemUser::getOrderId));

        return orders.stream().map(order -> {
            List<ResOrderUserDTO.OrderItemDTO> products = itemsByOrderId
                    .getOrDefault(order.getOrderId(), List.of())
                    .stream().map(item -> new ResOrderUserDTO.OrderItemDTO(
                            item.getProductId(),
                            item.getProductName(),
                            item.getOrderItemId(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getReviewed(),
                            item.getImageUrl()))
                    .collect(Collectors.toList());

            return new ResOrderUserDTO(
                    order.getOrderId(),
                    order.getTotalPrice(),
                    order.getStatus(),
                    order.getOrderDate(),
                    order.getPaymentMethod(),
                    order.getPaymentStatus(),
                    products);
        }).collect(Collectors.toList());
    }
}
