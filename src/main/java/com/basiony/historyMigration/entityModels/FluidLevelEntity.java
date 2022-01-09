package com.basiony.historyMigration.entityModels;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "fluidLevel")
public class FluidLevelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String wellName;
    private String actionKey;
    private String measurementDate;
    private String DynamicFluidLevel;
    private String staticFluidLevel;
    private String liquidPercentage;
    @Column(length = 1000)
    private String remarks;

}
