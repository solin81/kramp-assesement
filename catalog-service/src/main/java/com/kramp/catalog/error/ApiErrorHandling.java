package com.kramp.catalog.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

public final class ApiErrorHandling {
    private ApiErrorHandling() {
    }

    public record ApiError(Instant timestamp, int status, String error, String message, String path) {
    }

    @RestControllerAdvice
    public static class ApiExceptionHandler {
        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException exception, HttpServletRequest request) {
            return response(exception.getStatusCode(), exception.getReason(), request);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiError> handleUnexpected(Exception exception, HttpServletRequest request) {
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
        }

        private ResponseEntity<ApiError> response(HttpStatusCode status, String message, HttpServletRequest request) {
            var httpStatus = HttpStatus.resolve(status.value());
            var error = httpStatus != null ? httpStatus.getReasonPhrase() : "Error";
            return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), error, message, request.getRequestURI()));
        }
    }

    @RestController
    public static class ApiErrorController implements ErrorController {
        @RequestMapping("${server.error.path:${error.path:/error}}")
        public ResponseEntity<ApiError> error(HttpServletRequest request) {
            var status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) instanceof Integer value ? value : 500;
            var httpStatus = HttpStatus.resolve(status);
            var message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            var path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            return ResponseEntity.status(status).body(new ApiError(Instant.now(), status,
                    httpStatus != null ? httpStatus.getReasonPhrase() : "Error",
                    message != null && !message.isBlank() ? message : "Request failed",
                    path != null ? path : request.getRequestURI()));
        }
    }
}
