package com.basiony.historyMigration.buisnessModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WellMettaData {
    private String wellName;
    private String pumpDepth;
    private String suType;
    private String srType;
    private String srConfig;
    private String shSize;
    private String strokeLength;
    private String pumpSize;
    private String intervals;

    @Override
    public String toString() {
        return
                "well Name= " + wellName + ','+ '\n' +
                "Pump Depth= " + pumpDepth +','+  '\n' +
                "SU Type= " + suType + ','+ '\n' +
                "SR Type= " + srType + ','+ '\n' +
                "SR Config= " + srConfig +','+  '\n' +
                "sh. Size= " + shSize + ','+ '\n' +
                "Stroke Length= " + strokeLength + ','+  '\n' +
                "Pump Size= " + pumpSize + ','+ '\n' +
                "Intervals= " + intervals + ','+ '\n' +
                '}' + "\n************************************";
    }
}


