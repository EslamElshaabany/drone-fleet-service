package com.elmenus.fleet.repository;

import com.elmenus.fleet.entity.BatteryHistory;
import com.elmenus.fleet.entity.BatteryHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatteryHistoryRepository extends JpaRepository<BatteryHistory, BatteryHistoryId> {
}
