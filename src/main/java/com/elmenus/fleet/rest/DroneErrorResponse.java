package com.elmenus.fleet.rest;

public record DroneErrorResponse(
    int status,
    String message,
    Long timeStamp
) {
}
