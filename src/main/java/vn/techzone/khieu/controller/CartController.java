package vn.techzone.khieu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.ProductIdDTO;
import vn.techzone.khieu.dto.request.cart.UpdateQuantityCartDTO;
import vn.techzone.khieu.dto.response.cart.ResCartDTO;
import vn.techzone.khieu.dto.response.user.ResStringDTO;
import vn.techzone.khieu.service.CartService;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;
import vn.techzone.khieu.utils.annotation.RateLimit;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "4. Giỏ hàng (Cart)", description = "Thêm, sửa, xóa sản phẩm trong giỏ và tiến hành checkout")
public class CartController {
    private final CartService cartService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Thêm sản phẩm vào giỏ hàng")
    public ResponseEntity<Void> addProductToCart(@Valid @RequestBody ProductIdDTO productIdDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.addProductToCart(userId, productIdDTO.getProductId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/number-cart")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Lấy số lượng sản phẩm trong giỏ hàng")
    public ResponseEntity<Long> numberCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        Long numberCart = cartService.numberCart(userId);
        return ResponseEntity.ok(numberCart);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Lấy thông tin giỏ hàng")
    public ResponseEntity<List<ResCartDTO>> getCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PatchMapping("/update-quantity")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @RateLimit(capacity = 5, minutes = 1)
    @ApiMessage("Cập nhật số lượng sản phẩm trong giỏ hàng")
    public ResponseEntity<Void> updateQuantityCart(@Valid @RequestBody UpdateQuantityCartDTO updateQuantityCartDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.updateQuantityCart(userId, updateQuantityCartDTO.getProductId(),
                updateQuantityCartDTO.getQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Xóa sản phẩm khỏi giỏ hàng")
    public ResponseEntity<ResStringDTO> removeProductFromCart(@Valid @RequestBody ProductIdDTO productIdDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.deleteProductInCart(userId, productIdDTO.getProductId());
        return ResponseEntity.ok().body(new ResStringDTO("Xóa sản phẩm khỏi giỏ hàng thành công"));
    }

    @PostMapping("/buy-now")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Buy Now")
    public ResponseEntity<ResStringDTO> buyNow(@Valid @RequestBody ProductIdDTO productIdDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.buyNow(userId, productIdDTO.getProductId());
        return ResponseEntity.ok().body(new ResStringDTO("Mua ngay thành công"));
    }

    @PatchMapping("/checkout")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    @ApiMessage("Checkout sản phẩm trong giỏ hàng")
    public ResponseEntity<ResStringDTO> checkout() {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.checkout(userId);
        return ResponseEntity.ok().body(new ResStringDTO("Checkout thành công"));
    }
}
