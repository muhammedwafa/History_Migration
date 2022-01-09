package com.basiony.historyMigration.repo;


import com.basiony.historyMigration.entityModels.FluidLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FluidLevelRepository extends JpaRepository<FluidLevelEntity, Long> {
}
