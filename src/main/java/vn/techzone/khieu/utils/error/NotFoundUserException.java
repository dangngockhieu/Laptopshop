package vn.techzone.khieu.utils.error;

public class NotFoundUserException extends RuntimeException {

    public NotFoundUserException(String message) {
        super(message);
    }

}
