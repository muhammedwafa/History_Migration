package com.basiony.historyMigration.buisnessModels;

import lombok.*;

@Data
public class FluidLevel {
    private long id;
    private String wellName;
    private String actionKey;
    private String measurementDate;
    private String DynamicFluidLevel;
    private String staticFluidLevel;
    private String liquidPercentage;
    private String remarks;
}
