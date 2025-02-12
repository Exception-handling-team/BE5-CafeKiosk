package programmers.cafe.global.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import programmers.cafe.global.exception.*;
import programmers.cafe.global.wrapper.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    //v3
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> RuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(ex.getMessage(), null));
    }

    //v2
//
//    @ExceptionHandler(ItemNotFoundException.class)
//    public ResponseEntity<ApiResponse<Void>> handleItemNotFoundException(ItemNotFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(ex.getMessage(), null));
//    }
//
//    @ExceptionHandler(OverQuantityException.class)
//    public ResponseEntity<ApiResponse<Void>> handleOverQuantityException(OverQuantityException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(ex.getMessage(), null));
//    }

     //v1
//    @ExceptionHandler(TradeNotFoundException.class)
//    public ResponseEntity<String> handleTradeNotFoundException(TradeNotFoundException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(TradeCommonException.class)
//    public ResponseEntity<String> handleTradeCommonException(TradeCommonException exception) {
//        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
//    }
}
