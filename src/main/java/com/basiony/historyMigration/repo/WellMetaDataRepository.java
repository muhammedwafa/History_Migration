package com.basiony.historyMigration.repo;

import com.basiony.historyMigration.entityModels.WellMettaDataEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//@Repository ("WellMetaDataRepository")
public interface WellMetaDataRepository extends JpaRepository<WellMettaDataEntity, Integer> {
}
