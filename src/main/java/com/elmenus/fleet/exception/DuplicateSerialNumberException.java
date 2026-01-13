package com.elmenus.fleet.exception;

public class DuplicateSerialNumberException extends RuntimeException{
    public DuplicateSerialNumberException(String message) {
        super(message);
    }
}
