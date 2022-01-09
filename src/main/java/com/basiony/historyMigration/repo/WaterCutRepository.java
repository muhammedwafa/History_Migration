package com.basiony.historyMigration.repo;

import com.basiony.historyMigration.entityModels.WaterCutEntity;
import com.basiony.historyMigration.entityModels.WellTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaterCutRepository extends JpaRepository<WaterCutEntity, Long> {
}
