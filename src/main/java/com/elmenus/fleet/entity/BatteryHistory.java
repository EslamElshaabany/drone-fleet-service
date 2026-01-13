package com.elmenus.fleet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "battery_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryHistory implements Serializable {

  @EmbeddedId
  private BatteryHistoryId batteryHistoryId;

  @Column(
      name = "remaining_capacity",
      nullable = false,
      columnDefinition = "INT CHECK (remaining_capacity >= 0 AND remaining_capacity <= 100)"
  )
  private Integer remainingCapacity;

}
