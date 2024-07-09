package com.example.xuber.exceptions;

public class UnauthorizedDriverException extends RuntimeException {
    public UnauthorizedDriverException(String message) {
        super(message);
    }
}
