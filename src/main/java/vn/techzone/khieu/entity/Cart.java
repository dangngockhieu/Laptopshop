package vn.techzone.khieu.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carts", indexes = @Index(name = "idx_carts_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class Cart {

    @EmbeddedId
    private CartId id;

    @NotNull(message = "Number Product in Cart cannot be empty")
    private Integer number;

    private Boolean selected = false;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
}
