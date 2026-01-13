package com.elmenus.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drone_load")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DroneLoad {

  public enum LoadStatus {
    PENDING, ASSIGNED, REJECTED, DELIVERED
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "weight", nullable = false, columnDefinition = "DECIMAL(5,2) CHECK (weight > 0)")
  private Double weight;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private LoadStatus status;

  @Column(name = "message")
  private String message;

  @ManyToMany(cascade = {
      CascadeType.DETACH,
      CascadeType.MERGE,
      CascadeType.PERSIST,
      CascadeType.REFRESH
  })
  @JoinTable(name = "drone_load_has_medication",
      joinColumns = @JoinColumn(name = "load_id"),
      inverseJoinColumns = @JoinColumn(name = "medication_id")
  )
  private List<Medication> medications;

  public void addMedications(Medication med) {
    if (medications == null) {
      medications = new ArrayList<>();
    }
    medications.add(med);
  }

}
