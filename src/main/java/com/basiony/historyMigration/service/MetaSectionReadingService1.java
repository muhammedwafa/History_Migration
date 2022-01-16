package com.basiony.historyMigration.service;

import com.basiony.historyMigration.buisnessModels.WellMettaData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.List;

public interface MetaSectionReadingService1 {

    void loopOverSheets();

    // this method will format each cell as a string to help to read them.
    void formatRange(Sheet sheet);

    // reading the file content.
    void readHeaderSection(Sheet worksheet);

    //create a method that takes the business model,map it into entities and save to the database.
    void mapEntitiesToModals();

    // method to get all available business model classes
    List<WellMettaData> getAll();
}
