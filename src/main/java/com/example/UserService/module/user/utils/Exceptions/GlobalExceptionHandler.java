package com.example.UserService.module.user.utils.Exceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.UserService.module.user.utils.ResponseWrapper.ApiResponse;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        ApiResponse<?> response = ApiResponse.error(
            ex.getMessage(),
            ex.getCode(),
            null    
        );
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingBody() {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Request body is missing or invalid JSON", "400", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValid(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Validation failed", "400", null));
    }
}
