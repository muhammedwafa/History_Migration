package com.basiony.historyMigration.repo;

import com.basiony.historyMigration.entityModels.WellRemarksEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WellRemarksRepository extends JpaRepository<WellRemarksEntity, Long> {
}
