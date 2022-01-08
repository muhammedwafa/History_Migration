package com.basiony.historyMigration.entityModels;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "wellMetaData")
public class WellMettaDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    private String wellName;
    private String pumpDepth;
    private String suType;
    private String srType;
    private String srConfig;
    private String shSize;
    private String strokeLength;
    private String pumpSize;
    @Column(name = "producingIntervals",length = 1000)
    private String intervals;

}


