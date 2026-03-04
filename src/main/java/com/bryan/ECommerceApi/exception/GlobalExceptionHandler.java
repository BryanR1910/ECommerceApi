package com.bryan.ECommerceApi.exception;

import com.bryan.ECommerceApi.model.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handlerMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest wb){
        Map<String, String> mapErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((e) -> {
            String key = ((FieldError)e).getField();
            String value = e.getDefaultMessage();
            mapErrors.put(key, value);
        });
        ApiResponse apiResponse = new ApiResponse(wb.getDescription(false), mapErrors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handlerResourceNotFound(ResourceNotFoundException ex, WebRequest wb){
        ApiResponse apiResponse = new ApiResponse(wb.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

   @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handlerBadCredentials(BadCredentialsException ex, WebRequest wb){
        ApiResponse apiResponse = new ApiResponse(wb.getDescription(false), "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
   }

   @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handlerEmailAlreadyExists(EmailAlreadyExistsException ex, WebRequest wb){
        ApiResponse apiResponse = new ApiResponse(wb.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
   }

}
