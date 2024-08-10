// JwtExceptionHandler.java
package com.pfe.elearning.handler;

import com.pfe.elearning.exception.JwtExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JwtExceptionHandler {

    @ExceptionHandler(JwtExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleJwtExpiredException(JwtExpiredException ex) {
        //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ex.getMessage()");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("JWT Token has expired. Please login again.");

    }

    // Add more exception handlers if needed
}
