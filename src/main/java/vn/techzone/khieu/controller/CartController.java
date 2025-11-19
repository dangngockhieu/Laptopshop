package vn.techzone.khieu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.cart.ProductIdDTO;
import vn.techzone.khieu.dto.request.cart.UpdateQuantityCartDTO;
import vn.techzone.khieu.dto.response.cart.ResCartDTO;
import vn.techzone.khieu.service.CartService;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping()
    @ApiMessage("Thêm sản phẩm vào giỏ hàng")
    public ResponseEntity<Void> addProductToCart(@Valid @RequestBody ProductIdDTO productIdDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.addProductToCart(userId, productIdDTO.getProductId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/number-cart")
    @ApiMessage("Lấy số lượng sản phẩm trong giỏ hàng")
    public ResponseEntity<Long> numberCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        Long numberCart = cartService.numberCart(userId);
        return ResponseEntity.ok(numberCart);
    }

    @GetMapping
    @ApiMessage("Lấy thông tin giỏ hàng")
    public ResponseEntity<List<ResCartDTO>> getCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PatchMapping("/update-quantity")
    @ApiMessage("Cập nhật số lượng sản phẩm trong giỏ hàng")
    public ResponseEntity<Void> updateQuantityCart(@Valid @RequestBody UpdateQuantityCartDTO updateQuantityCartDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.updateQuantityCart(userId, updateQuantityCartDTO.getProductId(),
                updateQuantityCartDTO.getQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    @ApiMessage("Xóa sản phẩm khỏi giỏ hàng")
    public ResponseEntity<Void> removeProductFromCart(@Valid @RequestBody ProductIdDTO productIdDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.deleteProductInCart(userId, productIdDTO.getProductId());
        return ResponseEntity.ok().build();
    }
}
