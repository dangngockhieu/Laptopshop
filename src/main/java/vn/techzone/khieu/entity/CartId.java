package vn.techzone.khieu.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class CartId implements Serializable {

    private Integer userID;
    private Integer productID;
}
