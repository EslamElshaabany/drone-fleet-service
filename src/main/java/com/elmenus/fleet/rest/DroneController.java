package com.elmenus.fleet.rest;

import com.elmenus.fleet.dto.DroneDTO;
import com.elmenus.fleet.dto.LoadDTO;
import com.elmenus.fleet.entity.Drone;
import com.elmenus.fleet.entity.Medication;
import com.elmenus.fleet.service.DroneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drones")
@RequiredArgsConstructor
public class DroneController {

  private final DroneService droneService;

  @PostMapping
  public Drone registerDrone(@Valid @RequestBody DroneDTO droneDTO) {
    return droneService.registerDrone(droneDTO);
  }

  @PostMapping("/{id}/load")
  public Drone loadDrone(@PathVariable Long id, @Valid @RequestBody LoadDTO loadDTO) {
    return droneService.loadDrone(id, loadDTO);
  }

  @GetMapping("/{id}/load")
  public List<Medication> getLoadedMedications(@PathVariable Long id) {
    return droneService.getLoadedMedications(id);
  }

  @GetMapping
  public List<Drone> getAvailableDrones(@RequestParam(required = false) Drone.DroneStatus status) {
    return droneService.getAvailableDrones(status);
  }

  @GetMapping("/{id}/battery")
  public Integer checkBatteryLevel(@PathVariable Long id) {
    return droneService.checkBatteryLevel(id);
  }

}