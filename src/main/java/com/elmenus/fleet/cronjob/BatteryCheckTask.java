package com.elmenus.fleet.cronjob;

import com.elmenus.fleet.entity.BatteryHistory;
import com.elmenus.fleet.entity.BatteryHistoryId;
import com.elmenus.fleet.entity.Drone;
import com.elmenus.fleet.repository.BatteryHistoryRepository;
import com.elmenus.fleet.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class BatteryCheckTask {

  private final DroneRepository droneRepository;
  private final BatteryHistoryRepository batteryHistoryRepository;

  @Scheduled(cron = "0 0/1 * * * ?")
  public void checkBatteryLevels() {
    Timestamp now = Timestamp.from(Instant.now());

    droneRepository.findAll().stream()
        .map(drone -> createBatteryHistory(drone, now))
        .forEach(batteryHistoryRepository::save);
  }

  private BatteryHistory createBatteryHistory(Drone drone, Timestamp recordedAt) {
    return BatteryHistory.builder()
        .batteryHistoryId(BatteryHistoryId.builder()
            .drone(drone)
            .recordedAt(recordedAt)
            .build())
        .remainingCapacity(drone.getBatteryCapacity())
        .build();
  }

}
