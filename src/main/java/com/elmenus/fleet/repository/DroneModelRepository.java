package com.elmenus.fleet.repository;

import com.elmenus.fleet.entity.DroneModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroneModelRepository extends JpaRepository<DroneModel, Long> {

    DroneModel findByModel(String model);

}
