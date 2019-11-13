package com.manager.util.exception;

public abstract class EmailException extends Exception {
    private String message;

    public EmailException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
