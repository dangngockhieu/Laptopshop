package vn.techzone.khieu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Quantity cannot be empty")
    private Integer quantity;

    @NotEmpty(message = "Price cannot be empty")
    private Integer price;

    private Boolean isReviewed = false;

    @ManyToOne
    @JoinColumn(name = "orderID")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "productID")
    private Product product;
}
