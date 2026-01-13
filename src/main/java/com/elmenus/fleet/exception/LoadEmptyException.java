package com.elmenus.fleet.exception;

public class LoadEmptyException extends RuntimeException{
    public LoadEmptyException(String message) {
        super(message);
    }
}
