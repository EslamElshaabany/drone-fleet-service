package com.elmenus.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drone_model")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DroneModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "model", nullable = false, length = 50, unique = true)
  private String model;

  @Column(name = "max_weight", nullable = false, columnDefinition = "DECIMAL(5,2)")
  private Double maxWeight;

  public DroneModel(String model, Double maxWeight) {
    this.model = model;
    this.maxWeight = maxWeight;
  }

}
