package vn.techzone.khieu.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carts", indexes = @Index(name = "idx_carts_userID", columnList = "userID"))
@Getter
@Setter
@NoArgsConstructor
public class Cart {

    @EmbeddedId
    private CartId id;

    @NotEmpty(message = "Number Product in Cart cannot be empty")
    private Integer number;

    private Boolean isSelected = false;

    @ManyToOne
    @MapsId("userID")
    @JoinColumn(name = "userID")
    private User user;

    @ManyToOne
    @MapsId("productID")
    @JoinColumn(name = "productID")
    private Product product;
}
