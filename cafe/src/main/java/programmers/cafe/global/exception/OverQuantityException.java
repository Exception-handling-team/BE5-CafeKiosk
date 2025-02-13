package programmers.cafe.global.exception;

public class OverQuantityException extends RuntimeException {
    public OverQuantityException(String message) {
        super(message);
    }

    public OverQuantityException() {
        super("준비된 상품의 수량이 부족합니다.");
    }
}

