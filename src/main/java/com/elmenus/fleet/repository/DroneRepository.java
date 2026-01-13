package com.elmenus.fleet.repository;

import com.elmenus.fleet.entity.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DroneRepository extends JpaRepository<Drone, Long> {

    List<Drone> findByStatus(Drone.DroneStatus status);

    boolean existsBySerialNumber(String serialNumber);

}
