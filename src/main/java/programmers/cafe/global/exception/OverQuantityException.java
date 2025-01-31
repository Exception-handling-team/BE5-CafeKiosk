package programmers.cafe.global.exception;

public class OverQuantityException extends RuntimeException {
    public OverQuantityException(String message) {
        super(message);
    }
}

