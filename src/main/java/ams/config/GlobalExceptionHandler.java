package ams.config;

import ams.exception.InvalidEnumValueException;
import ams.exception.ResourceNotFoundException;
import ams.exception.TokenExpiredException;
import ams.model.dto.FieldError;
import ams.resource.BaseResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends BaseResource {

    @ExceptionHandler({ResourceNotFoundException.class})
    public String notFoundHandler(Exception e) {

        return "/error/404";
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> methodArgumentNotValidHandle(MethodArgumentNotValidException e) {
        List<FieldError> errorList = e.getBindingResult().getFieldErrors().stream()
                .map(objectError -> new FieldError(objectError.getField(), objectError.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(errorList);
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<?> ioExceptionHandle(IOException e){
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler({TokenExpiredException.class})
    public ResponseEntity<?> tokenExpiredHandle(TokenExpiredException e){
        return forbidden(e.getMessage());
    }


}
