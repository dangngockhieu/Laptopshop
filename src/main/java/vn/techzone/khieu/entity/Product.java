package vn.techzone.khieu.entity;

import java.util.List;
import java.util.Set;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "OriginalPrice cannot be empty")
    private Integer originalPrice;

    private Integer price;

    private Integer coupon;

    @NotNull(message = "Quantity cannot be empty")
    private Integer quantity;

    private Integer sold;

    private String warranty;

    @Column(columnDefinition = "TEXT")
    private String infor;

    private String cpu;

    private String ram;

    private String storage;

    private String screen;

    private String graphicsCard;

    private String battery;

    private String weight;

    private String releaseYear;

    private String category;

    private String factory;

    @OneToMany(mappedBy = "product")
    @BatchSize(size = 10)
    private Set<ProductImage> images;

    @OneToMany(mappedBy = "product")
    private List<Cart> carts;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product")
    @BatchSize(size = 10)
    private Set<Review> reviews;

    @OneToMany(mappedBy = "product")
    @BatchSize(size = 10)
    private Set<ProductFeature> features;
}
