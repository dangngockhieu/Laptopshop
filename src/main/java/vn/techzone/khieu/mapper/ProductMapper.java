package vn.techzone.khieu.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import vn.techzone.khieu.dto.request.product.CreateProductDTO;
import vn.techzone.khieu.dto.response.product.ResProductDTO;
import vn.techzone.khieu.entity.Product;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    Product toCreateProduct(CreateProductDTO dto);

    ResProductDTO toResProductDTO(Product product);
}
