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
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("featureId")
    @JoinColumn(name = "feature_id")
    private Feature feature;
}
