package com.example.kata.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j  // Для логирования
@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Обработка ошибок "Ресурс не найден" (404 Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("ResourceNotFoundException: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 2. Обработка ошибок "Неверное состояние запроса" (400 Bad Request)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 3. Обработка ошибок валидации (400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        // Сбор всех ошибок валидации
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        response.put("message", "Некорректные данные в запросе");
        response.put("errors", fieldErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 4. Обработка ошибок авторизации (401 Unauthorized)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Unauthorized error: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Неверные учетные данные. Проверьте логин и пароль.");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 5. Обработка ошибок доступа (403 Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", "У вас нет прав для выполнения данного действия.");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 6. Обработка всех остальных ошибок (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        Map<String, String> response = new HashMap<>();
        response.put("message", "На сервере произошла ошибка. Попробуйте позже.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}