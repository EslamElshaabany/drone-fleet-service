package com.elmenus.fleet.service;

import com.elmenus.fleet.dto.DroneDTO;
import com.elmenus.fleet.dto.LoadDTO;
import com.elmenus.fleet.entity.Drone;
import com.elmenus.fleet.entity.Medication;

import java.util.List;

public interface DroneService {

  Drone registerDrone(DroneDTO droneDTO);

  Drone loadDrone(Long id, LoadDTO loadDTO);

  List<Medication> getLoadedMedications(Long id);

  List<Drone> getAvailableDrones(Drone.DroneStatus status);

  Integer checkBatteryLevel(Long id);

}
