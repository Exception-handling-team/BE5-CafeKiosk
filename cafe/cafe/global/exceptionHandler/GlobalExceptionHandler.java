package programmers.cafe.global.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import programmers.cafe.exception.ItemNotFoundException;
import programmers.cafe.exception.OverQuantityException;
import programmers.cafe.exception.PayRefusedException;
import programmers.cafe.exception.TradeNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<String> handleItemNotFoundException(ItemNotFoundException ex) {
        // 로깅 추가 가능
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OverQuantityException.class)
    public ResponseEntity<String> handleOverQuantityException(OverQuantityException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PayRefusedException.class)
    public ResponseEntity<String> handlePayRefusedException(PayRefusedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TradeNotFoundException.class)
    public ResponseEntity<String> handleTradeNotFoundException(TradeNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    // 다른 예외 처리 핸들러도 추가 가능
}
