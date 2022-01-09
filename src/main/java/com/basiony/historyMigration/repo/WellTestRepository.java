package com.basiony.historyMigration.repo;

import com.basiony.historyMigration.entityModels.WellTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WellTestRepository extends JpaRepository<WellTestEntity, Long> {
}
