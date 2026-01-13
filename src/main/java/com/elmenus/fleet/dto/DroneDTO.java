package com.elmenus.fleet.dto;

import com.elmenus.fleet.entity.Drone;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DroneDTO(

    @NotNull
    @Size(max = 100)
    String serialNumber,

    @NotNull
    @Min(0)
    @Max(100)
    Integer batteryCapacity,

    Drone.DroneStatus status,

    @NotNull
    @Size(max = 50)
    String droneModel

) {
}
