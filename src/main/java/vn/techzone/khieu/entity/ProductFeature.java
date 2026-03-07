package vn.techzone.khieu.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_features")
@Getter
@Setter
@NoArgsConstructor
public class ProductFeature {

    @EmbeddedId
    private ProductFeatureId id;

    @ManyToOne
    @MapsId("productID")
    @JoinColumn(name = "productID")
    private Product product;

    @ManyToOne
    @MapsId("featureID")
    @JoinColumn(name = "featureID")
    private Feature feature;
}
