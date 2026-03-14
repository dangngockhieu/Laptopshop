package vn.techzone.khieu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.response.cart.ResCartDTO;
import vn.techzone.khieu.entity.Cart;
import vn.techzone.khieu.entity.CartId;
import vn.techzone.khieu.repository.CartRepository;
import vn.techzone.khieu.repository.ProductRepository;
import vn.techzone.khieu.repository.UserRepository;
import vn.techzone.khieu.utils.error.FailRequestException;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public boolean isProductInCart(Long userId, Long productId) {
        return cartRepository.existsByIdUserIdAndIdProductId(userId, productId);
    }

    public void addProductToCart(Long userId, Long productId) {
        if (!productRepository.existsByIdAndQuantityGreaterThan(productId, 0)) {
            throw new FailRequestException("Sản phẩm không tồn tại hoặc đã hết hàng");
        }

        CartId cartId = new CartId(userId, productId);
        Optional<Cart> existingCart = cartRepository.findById(cartId);

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setNumber(cart.getNumber() + 1);
            cartRepository.save(cart);
        } else {
            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setNumber(1);
            cart.setUser(userRepository.getReferenceById(userId));
            cart.setProduct(productRepository.getReferenceById(productId));
            cartRepository.save(cart);
        }
    }

    public Long numberCart(Long userId) {
        return cartRepository.countByIdUserId(userId);
    }

    public List<ResCartDTO> getCart(Long userId) {
        return cartRepository.getCart(userId);
    }

    public void updateQuantityCart(Long userId, Long productId, Integer quantity) {
        CartId cartId = new CartId(userId, productId);
        Optional<Cart> existingCart = cartRepository.findById(cartId);

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setNumber(quantity);
            cartRepository.save(cart);
        } else {
            throw new FailRequestException("Sản phẩm không tồn tại trong giỏ hàng");
        }
    }

    public void deleteProductInCart(Long userId, Long productId) {
        CartId cartId = new CartId(userId, productId);
        if (cartRepository.existsById(cartId)) {
            cartRepository.deleteById(cartId);
        } else {
            throw new FailRequestException("Sản phẩm không tồn tại trong giỏ hàng");
        }
    }

    public void buyNow(Long userId, Long productId) {
        if (!productRepository.existsByIdAndQuantityGreaterThan(productId, 0)) {
            throw new FailRequestException("Sản phẩm không tồn tại hoặc đã hết hàng");
        }

        CartId cartId = new CartId(userId, productId);
        Optional<Cart> existingCart = cartRepository.findById(cartId);

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setSelected(true);
            cartRepository.save(cart);
        } else {
            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setSelected(true);
            cart.setNumber(1);
            cart.setUser(userRepository.getReferenceById(userId));
            cart.setProduct(productRepository.getReferenceById(productId));
            cartRepository.save(cart);
        }
    }

    public void checkout(Long userId, Long productId) {
        CartId cartId = new CartId(userId, productId);
        Optional<Cart> existingCart = cartRepository.findById(cartId);
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setSelected(false);
            cartRepository.save(cart);
        } else {
            throw new FailRequestException("Sản phẩm không tồn tại trong giỏ hàng");
        }
    }

}
