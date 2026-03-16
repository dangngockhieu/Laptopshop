package vn.techzone.khieu.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import vn.techzone.khieu.dto.request.product.CreateProductDTO;
import vn.techzone.khieu.dto.response.product.ResProductDTO;
import vn.techzone.khieu.entity.Product;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    Product toCreateProduct(CreateProductDTO dto);

    @Mapping(target = "features", expression = "java(mapFeatures(product))")
    ResProductDTO toResProductDTO(Product product);

    default List<Long> mapFeatures(Product product) {
        if (product.getFeatures() == null)
            return Collections.emptyList();
        return product.getFeatures()
                .stream()
                .map(pf -> pf.getFeature().getId())
                .collect(Collectors.toList());
    }
}