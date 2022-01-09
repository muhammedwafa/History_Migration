package com.basiony.historyMigration.buisnessModels;

import lombok.*;

@Data
public class WaterCut {
    private long id;
    private String wellName;
    private String measurementDate;
    private double waterCut;
    private String comments;

}
