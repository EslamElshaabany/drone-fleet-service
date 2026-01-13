package com.elmenus.fleet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryHistoryId implements Serializable {

  @ManyToOne
  @JoinColumn(name = "drone_id", nullable = false)
  private Drone drone;

  @Column(name = "recorded_at", nullable = false)
  private Timestamp recordedAt;
  
}
