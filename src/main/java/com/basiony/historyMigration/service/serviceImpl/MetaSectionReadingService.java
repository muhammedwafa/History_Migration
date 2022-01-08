package com.basiony.historyMigration.service.serviceImpl;

import com.basiony.historyMigration.buisnessModels.WellMettaData;
import com.basiony.historyMigration.entityModels.WellMettaDataEntity;
import com.basiony.historyMigration.repo.WellMetaDataRepository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetaSectionReadingService {
    final String filePath = "src/main/resources/static/aman wells.xlsx";
    private XSSFWorkbook workbook;
    private WellMettaData well1MetaData;
    private final List<WellMettaData> wellsMetaData = new ArrayList<>();
    private final List<WellMettaDataEntity> entities = new ArrayList<>();

    WellMetaDataRepository wellMetaDataRepository;

    //default constructor
    @Autowired
    public MetaSectionReadingService(WellMetaDataRepository wellMetaDataRepository) {
        this.wellMetaDataRepository = wellMetaDataRepository;
        try {
            workbook = new XSSFWorkbook(new File(filePath));
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }


    public void loopOverSheets(){
        for (Sheet sheet : workbook) {
            formatRange(sheet);
            readMetaData(sheet);
        }
        mapData();
        printItems();
    }

    // this method will format each cell as a string to help to read them.
    public void formatRange(Sheet sheet) {
        for (int i = 0; i < 6; i++) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    cell.setCellType(CellType.STRING);
                }
            }
        }
    }

    // reading the file content.
    public void readMetaData(Sheet worksheet) {

        // creating new object for each sheet.
        System.out.println("started looping");
        well1MetaData = new WellMettaData();
        well1MetaData.setWellName(worksheet.getSheetName());
        int rowNumber;
        for (int i = 1; i < 5; i++) {
            rowNumber = i;
            switch (rowNumber) {
                case 1:// read the following data (pump depth, sh size
                    well1MetaData.setPumpDepth(worksheet.getRow(rowNumber).getCell(2).getStringCellValue());
                    well1MetaData.setShSize(worksheet.getRow(rowNumber).getCell(10).getStringCellValue());
                    break;

                case 2: // read the following data (surface unit type, and stroke length
                    well1MetaData.setSuType(worksheet.getRow(rowNumber).getCell(2).getStringCellValue());
                    well1MetaData.setStrokeLength(worksheet.getRow(rowNumber).getCell(10).getStringCellValue());
                    break;

                case 3: // read the following data (S/R TYPE , pump size
                    well1MetaData.setSrType(worksheet.getRow(rowNumber).getCell(2).getStringCellValue());
                    well1MetaData.setPumpSize(worksheet.getRow(rowNumber).getCell(10).getStringCellValue());
                    break;

                case 4: // read the following data (S/R TYPE , pump size
                    well1MetaData.setSrConfig(worksheet.getRow(rowNumber).getCell(2).getStringCellValue());
                    well1MetaData.setIntervals(worksheet.getRow(rowNumber).getCell(10).getStringCellValue());
                    break;

                default:
                    System.out.println("sorry got nothing.");
                    break;
            }
        }
        // adding the created well object to the list of object.
        wellsMetaData.add(well1MetaData);

    }

    // print the list content.
    void printItems() {
        wellsMetaData.forEach(System.out::println);
    }

    //create a method that takes the business model,map it into entities and save to the database.
    public void mapData() {
        if (wellsMetaData.size() > 0) {
            wellsMetaData.forEach(x -> {
                WellMettaDataEntity entity = new WellMettaDataEntity();
                entity.setWellName(x.getWellName());
                entity.setPumpDepth(x.getPumpDepth());
                entity.setSuType(x.getSuType());
                entity.setSrType(x.getSrType());
                entity.setSrConfig(x.getSrConfig());
                entity.setShSize(x.getShSize());
                entity.setStrokeLength(x.getStrokeLength());
                entity.setPumpSize(x.getPumpSize());
                entity.setIntervals(x.getIntervals());
                entities.add(entity);
            });
        }
        wellMetaDataRepository.saveAll(entities);
    }

    // method to get all available business model classes
    public List<WellMettaData> getAll(){
        return this.wellsMetaData;
    }

}

