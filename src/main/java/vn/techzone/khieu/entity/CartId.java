package vn.techzone.khieu.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class CartId implements Serializable {

    private Long userID;
    private Long productID;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CartId that = (CartId) o;
        return Objects.equals(userID, that.userID) &&
                Objects.equals(productID, that.productID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, productID);
    }
}
