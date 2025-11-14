package vn.techzone.khieu.service.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.techzone.khieu.utils.RestResponse;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = NotFoundUserException.class)
    public ResponseEntity<RestResponse<Object>> handleNotFoundUserException(NotFoundUserException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }
}
