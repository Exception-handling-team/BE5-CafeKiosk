package programmers.cafe.exception;

public class PayRefusedException extends RuntimeException {
    public PayRefusedException(String message) {
        super(message);
    }
}

