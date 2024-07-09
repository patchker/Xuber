package com.example.xuber.exceptions;


public class ActiveRideExistsException extends RuntimeException {
    public ActiveRideExistsException(String message) {
        super(message);
    }
}