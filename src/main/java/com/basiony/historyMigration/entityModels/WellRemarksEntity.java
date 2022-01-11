package com.basiony.historyMigration.entityModels;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Table(name = "wellRemarks")
public class WellRemarksEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String wellName;
    private String actionKey;
    private Date actionDate;
    private String operationalComment;
    @Column(length = 5000)
    private String remarks;

}
