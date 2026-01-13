package com.elmenus.fleet.service;

import com.elmenus.fleet.dto.DroneDTO;
import com.elmenus.fleet.dto.LoadDTO;
import com.elmenus.fleet.entity.Drone;
import com.elmenus.fleet.entity.DroneLoad;
import com.elmenus.fleet.entity.DroneModel;
import com.elmenus.fleet.entity.Medication;
import com.elmenus.fleet.exception.*;
import com.elmenus.fleet.repository.DroneLoadRepository;
import com.elmenus.fleet.repository.DroneModelRepository;
import com.elmenus.fleet.repository.DroneRepository;
import com.elmenus.fleet.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DroneServiceImpl implements DroneService {

  private final DroneRepository droneRepository;
  private final DroneLoadRepository droneLoadRepository;
  private final MedicationRepository medicationRepository;
  private final DroneModelRepository droneModelRepository;

  @Override
  public Drone registerDrone(DroneDTO droneDTO) {
    if (droneRepository.existsBySerialNumber(droneDTO.serialNumber())) {
      throw new DuplicateSerialNumberException("Drone with the provided serial number already exists");
    }
    DroneModel droneModel = droneModelRepository.findByModel(droneDTO.droneModel());
    if (droneModel == null) {
      throw new NotFoundException(DroneModel.class.getSimpleName(), droneDTO.droneModel());
    }
    Drone drone = new Drone();
    drone.setSerialNumber(droneDTO.serialNumber());
    drone.setBatteryCapacity(droneDTO.batteryCapacity());
    drone.setStatus(droneDTO.status() == null ? Drone.DroneStatus.IDLE : droneDTO.status());
    drone.setDroneModel(droneModel);
    return droneRepository.save(drone);
  }

  @Override
  public Drone loadDrone(Long id, LoadDTO loadDTO) {
    Drone drone = droneRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Drone.class.getSimpleName(), id));

    if (!drone.getStatus().equals(Drone.DroneStatus.IDLE)) {
      throw new DroneLoadingException("Drone is not in IDLE state");
    }

    DroneLoad droneLoad = new DroneLoad();
    Double totalWeight = 0d;

    if (loadDTO.medicationCodes() == null || loadDTO.medicationCodes().isEmpty()) {
      throw new LoadEmptyException("Load is empty of medication");
    }

    for (String code : loadDTO.medicationCodes()) {
      Medication med = medicationRepository.findByCode(code);
      if (med == null) {
        throw new NotFoundException(Medication.class.getSimpleName(), code);
      }
      totalWeight += med.getWeight();
      droneLoad.addMedications(med);
    }

    droneLoad.setWeight(totalWeight);

    if (drone.getDroneModel().getMaxWeight() < droneLoad.getWeight()) {
      droneLoad.setStatus(DroneLoad.LoadStatus.REJECTED);
      droneLoad.setMessage("loading failed: weight is more than the drone can carry");
      droneLoadRepository.save(droneLoad);
      throw new DroneLoadingException(droneLoad.getMessage());
    }

    if (drone.getBatteryCapacity() < 25) {
      droneLoad.setStatus(DroneLoad.LoadStatus.REJECTED);
      droneLoad.setMessage("loading failed: drone battery is low");
      droneLoadRepository.save(droneLoad);
      throw new DroneLoadingException(droneLoad.getMessage());
    }
    droneLoad.setStatus(DroneLoad.LoadStatus.ASSIGNED);
    DroneLoad savedDroneLoad = droneLoadRepository.save(droneLoad);
    drone.setDroneLoad(savedDroneLoad);
    drone.setStatus(Drone.DroneStatus.LOADED);
    return droneRepository.save(drone);
  }

  @Override
  public List<Medication> getLoadedMedications(Long id) {
    Drone drone = droneRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Drone.class.getSimpleName(), id));
    DroneLoad load = drone.getDroneLoad();
    if (load == null) {
      throw new DroneNotLoadedException("The drone doesn't have any load");
    }
    return load.getMedications();
  }

  @Override
  public List<Drone> getAvailableDrones(Drone.DroneStatus status) {
    return status == null ?
        droneRepository.findAll() :
        droneRepository.findByStatus(status);
  }

  @Override
  public Integer checkBatteryLevel(Long id) {
    Drone drone = droneRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Drone.class.getSimpleName(), id));
    return drone.getBatteryCapacity();
  }
}
