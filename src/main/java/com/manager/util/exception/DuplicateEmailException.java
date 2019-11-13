package com.manager.util.exception;

public class DuplicateEmailException extends EmailException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
