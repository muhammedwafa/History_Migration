package com.basiony.historyMigration.buisnessModels;

import lombok.*;


@Data
public class WellRemarks {
    private long id;
    private String wellName;
    private String actionKey;
    private String actionDate;
    private String operationalComment;
    private String remarks;
}
