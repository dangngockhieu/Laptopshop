package vn.techzone.khieu.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "OriginalPrice cannot be empty")
    private Integer originalPrice;

    private Integer price;

    private Integer coupon;

    @NotEmpty(message = "Quantity cannot be empty")
    private Integer quantity;

    private Integer sold;

    private String warranty;

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
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product")
    private List<Cart> carts;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @OneToMany(mappedBy = "product")
    private List<ProductFeature> features;
}
