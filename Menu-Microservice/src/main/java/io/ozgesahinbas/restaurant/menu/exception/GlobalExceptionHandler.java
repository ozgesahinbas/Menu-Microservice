package io.ozgesahinbas.restaurant.menu.exception;

import io.ozgesahinbas.restaurant.menu.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Translates exceptions into a single {@link ErrorResponse} shape so callers
 * never have to parse two different error formats.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MenuNotFoundException.class, MenuItemNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception,
                                                        HttpServletRequest request) {

        return build(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                          HttpServletRequest request) {

        Map<String, String> validationErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            validationErrors.put(
                    fieldError.getField(),
                    Objects.requireNonNullElse(fieldError.getDefaultMessage(), "Invalid value"));
        }

        return build(HttpStatus.BAD_REQUEST, "Request validation failed", request, validationErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception,
                                                              HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST, "Malformed request body", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception,
                                                          HttpServletRequest request) {

        // Spring MVC's own failures - unknown path, unsupported HTTP method or
        // media type - already carry the right status and all implement
        // ErrorResponse. Matching on the interface keeps them out of the 500
        // bucket without having to enumerate every exception type.
        if (exception instanceof org.springframework.web.ErrorResponse errorResponse) {
            return build(HttpStatus.valueOf(errorResponse.getStatusCode().value()),
                    exception.getMessage(), request, null);
        }

        log.error("Unhandled exception while serving {}", request.getRequestURI(), exception);

        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status,
                                                String message,
                                                HttpServletRequest request,
                                                Map<String, String> validationErrors) {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
