package com.elmenus.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drone")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drone {

  public enum DroneStatus {
    IDLE, LOADING, LOADED, DELIVERING, DELIVERED, RETURNING
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "serial_number", length = 100, nullable = false)
  private String serialNumber;

  @Column(name = "battery_capacity", nullable = false, columnDefinition = "INT CHECK (battery_capacity >= 0 AND battery_capacity <= 100)")
  private Integer batteryCapacity;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private DroneStatus status;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "drone_model_id", nullable = false)
  private DroneModel droneModel;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "load_id")
  private DroneLoad droneLoad;

  public Drone(String serialNumber, Integer batteryCapacity, DroneStatus status, DroneModel droneModel) {
    this.serialNumber = serialNumber;
    this.batteryCapacity = batteryCapacity;
    this.status = status;
    this.droneModel = droneModel;
  }

}
