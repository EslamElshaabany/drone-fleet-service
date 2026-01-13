package com.elmenus.fleet.exception;

public class NotFoundException extends RuntimeException{
     public NotFoundException(String entityName, Object id) {
        super(String.format("%s not found - %s", entityName, id.toString()));
    }
}
