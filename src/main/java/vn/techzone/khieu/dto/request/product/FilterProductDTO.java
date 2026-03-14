package vn.techzone.khieu.dto.request.product;

import java.util.List;

import lombok.Data;

@Data
public class FilterProductDTO {
    String category;
    List<String> factories;
    List<Long> productFeatures;
    Long minPrice;
    Long maxPrice;
    List<String> cpu;
    List<String> ram;
    List<String> gpu;
    List<String> storage;
    List<String> screenSize;
    List<String> battery;
    List<String> screen;
}
