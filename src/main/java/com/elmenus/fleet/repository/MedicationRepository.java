package com.elmenus.fleet.repository;

import com.elmenus.fleet.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    Medication findByCode(String code);

}
