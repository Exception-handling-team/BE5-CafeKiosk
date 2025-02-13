package programmers.cafe.global.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException() {
        super("상품을 찾을 수 없습니다.");
    }
}

