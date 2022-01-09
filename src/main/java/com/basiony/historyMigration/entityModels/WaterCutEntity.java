package com.basiony.historyMigration.entityModels;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "waterCut")
public class WaterCutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String wellName;
    private String measurementDate;
    private double waterCut;
    @Column(length = 1000)
    private String comments;

}
