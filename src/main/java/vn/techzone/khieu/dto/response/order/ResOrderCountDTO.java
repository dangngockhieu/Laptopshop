package vn.techzone.khieu.dto.response.order;

public interface ResOrderCountDTO {
    Long getCount();

    Long getCountPending();

    Long getCountShipping();

    Long getCountCompleted();
}
