package programmers.cafe.global.exception;

public class TradeNotFoundException extends RuntimeException {
    public TradeNotFoundException(String message) {
        super(message);
    }
}

