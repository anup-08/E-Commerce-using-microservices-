package product_Service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException error){
        Map<String,String> errorMap = new HashMap<>();

        BindingResult result = error.getBindingResult();
        List<FieldError> fieldErrorList = result.getFieldErrors();

        for(FieldError fieldError : fieldErrorList){
            errorMap.put(fieldError.getField() , fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex , WebRequest request){
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotFound.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFound ex , WebRequest request){
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ProductNotFound.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFound ex , WebRequest request){
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex , WebRequest request){
        ErrorResponse response = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
    }
}
