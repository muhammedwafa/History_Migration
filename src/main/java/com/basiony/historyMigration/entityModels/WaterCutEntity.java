package com.basiony.historyMigration.entityModels;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Table(name = "waterCut")
public class WaterCutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String wellName;
    private Date measurementDate;
    private String waterCut;
    @Column(length = 1000)
    private String comments;

}
