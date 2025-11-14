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
public class ProductFeatureId implements Serializable {

    private Long productID;
    private Long featureID;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductFeatureId that = (ProductFeatureId) o;
        return Objects.equals(productID, that.productID) &&
                Objects.equals(featureID, that.featureID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productID, featureID);
    }
}
