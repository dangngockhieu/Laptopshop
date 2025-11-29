package vn.techzone.khieu.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.review.CreateReviewDTO;
import vn.techzone.khieu.entity.Product;
import vn.techzone.khieu.entity.Review;
import vn.techzone.khieu.repository.OrderItemRepository;
import vn.techzone.khieu.repository.ProductRepository;
import vn.techzone.khieu.repository.ReviewRepository;
import vn.techzone.khieu.repository.UserRepository;
import vn.techzone.khieu.utils.error.NotFindException;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public void createReview(Long userId, CreateReviewDTO createReviewDTO) {
        Long alreadyReviewed = orderItemRepository.getOrderItemNotReview(createReviewDTO.getOrderItemId(), userId);
        if (alreadyReviewed == null) {
            throw new BadCredentialsException(
                    "You have already reviewed this product or you are not eligible to review");
        }
        Product product = productRepository.findById(createReviewDTO.getProductId())
                .orElseThrow(
                        () -> new NotFindException("Product not found with id: " + createReviewDTO.getProductId()));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(userRepository.getReferenceById(userId));
        review.setRating(createReviewDTO.getRating());
        review.setComment(createReviewDTO.getComment());

        reviewRepository.save(review);
    }
}
