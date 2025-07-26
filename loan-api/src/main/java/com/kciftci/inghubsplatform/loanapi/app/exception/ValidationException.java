package com.kciftci.inghubsplatform.loanapi.app.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}