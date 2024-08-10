package com.pfe.elearning.exception;

public class OperationNonPermittedException extends RuntimeException {
    public OperationNonPermittedException(String message) {
        super(message);
    }
}
/*
-This exception is thrown when a non-permitted operation is detected.
 */