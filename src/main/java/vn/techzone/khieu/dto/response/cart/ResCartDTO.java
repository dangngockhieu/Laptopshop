package vn.techzone.khieu.dto.response.cart;

public interface ResCartDTO {
    Long getId();

    String getName();

    Integer getPrice();

    Integer getQuantity();

    Integer getOriginalPrice();

    Integer getNumber();

    Boolean getSelected();

    String getImageUrl();
}
