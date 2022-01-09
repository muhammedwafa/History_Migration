package com.basiony.historyMigration.entityModels;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "wellTest")
public class WellTestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String wellName;
    private String actionKey;
    private String measurementDate;
    private double gross;
    private double net;
    private double wc;
    private String gor;
    private String sPressure;
    @Column(length = 5000)
    private String remarks;
    
}
