package com.basiony.historyMigration.service.serviceImpl;


import com.basiony.historyMigration.entityModels.FluidLevelEntity;
import com.basiony.historyMigration.entityModels.WaterCutEntity;
import com.basiony.historyMigration.entityModels.WellRemarksEntity;
import com.basiony.historyMigration.entityModels.WellTestEntity;
import com.basiony.historyMigration.repo.FluidLevelRepository;
import com.basiony.historyMigration.repo.WaterCutRepository;

import com.basiony.historyMigration.repo.WellTestRepository;
import com.basiony.historyMigration.service.BodySectionReadingService;
import com.basiony.historyMigration.utils.SpreadSheetUtility;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Date;
import java.sql.SQLData;
import java.util.*;
import java.text.SimpleDateFormat;


@Service
public class BodySectionReadingServiceImpl implements BodySectionReadingService {


    // here uploadFolder contains the well history data
    final String filePath = "src/main/resources/static/ne.xlsx";
    final int dateColumnIndex = 0;

    XSSFWorkbook workbook;
    XSSFSheet sheet;

    ArrayList<WaterCutEntity> waterCutEntities = new ArrayList<>();
    ArrayList<WellTestEntity> wellTestEntities = new ArrayList<>();
    ArrayList<FluidLevelEntity> fluidLevelEntities = new ArrayList<>();
    WellTestRepository wellTestRepository;
    WaterCutRepository waterCutRepository;
    FluidLevelRepository fluidLevelRepository;
    SpreadSheetUtility sheetUtility;

    @Autowired
    public BodySectionReadingServiceImpl(WellTestRepository wellTestRepository, WaterCutRepository waterCutRepository, FluidLevelRepository fluidLevelRepository) {
        this.wellTestRepository = wellTestRepository;
        this.waterCutRepository = waterCutRepository;
        this.fluidLevelRepository = fluidLevelRepository;
        sheetUtility = new SpreadSheetUtility();
    }

    @Override
    public void readBodySection() {
        final int keyColumnIndex = 1;

        try (InputStream file = new FileInputStream(filePath)) {

            //Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(file);

            //Get desired sheets from the workbook
            for (Sheet currentWorkSheet : workbook) {
                sheet = (XSSFSheet) currentWorkSheet;
                int rowNumbers = sheetUtility.actualRowNumbers(sheet);
                System.out.println(" ------------------Reading data for Well : " + currentWorkSheet.getSheetName() + " -----------------------------");
                System.out.println("The actual row count is  : " + rowNumbers);

                for (int i = 6; i < rowNumbers - 1; i++) {
                    XSSFRow currentRow = sheet.getRow(i);
                    XSSFCell dateCell = currentRow.getCell(dateColumnIndex);
                    dateCell.setCellType(CellType.NUMERIC);
                    sheetUtility.manageMergedCells(sheet, dateCell);
                    XSSFCell keyCell = currentRow.getCell(keyColumnIndex);
                    getHistoryObjects(currentWorkSheet, currentRow, keyCell);
                }
            }

            waterCutEntities.forEach(System.out::println);
            wellTestEntities.forEach(System.out::println);
            fluidLevelEntities.forEach(System.out::println);

            wellTestRepository.saveAll(wellTestEntities);
            fluidLevelRepository.saveAll(fluidLevelEntities);
            waterCutRepository.saveAll(waterCutEntities);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /***
     * This method should loop over the key column and based on the key value it will create one of the following objects:
     * well test object, fluid level object , water cut measurements object and/or well remarks object.
     */
    private void getHistoryObjects(Sheet sheet, XSSFRow row, XSSFCell keyCell) {

        String key = keyCell.getStringCellValue().toUpperCase();
        switch (key) {
            case "TEST": {
                WellTestEntity entity = new WellTestEntity();
                entity.setWellName(sheet.getSheetName());

                double date = row.getCell(dateColumnIndex).getNumericCellValue();
                entity.setMeasurementDate(new java.sql.Date((long) (date - 25569) * 86400 * 1000));
                entity.setActionKey("Well Test");
                System.out.println(row.getRowNum());
                Cell g  = row.getCell(2);
                g.setCellType(CellType.NUMERIC);
                Cell w  = row.getCell(4);
                w.setCellType(CellType.NUMERIC);

                entity.setGross(row.getCell(2).getNumericCellValue());
                entity.setWc(row.getCell(4).getNumericCellValue());
                entity.setNet(Math.round(entity.getGross() * (1 - (entity.getWc()) / 100)));

                Cell gorCell = row.getCell(5, Row.CREATE_NULL_AS_BLANK);
                switch (gorCell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK: {
                        entity.setSPressure(String.valueOf(""));
                        break;
                    }
                    case Cell.CELL_TYPE_NUMERIC: {
                        entity.setGor(String.valueOf(row.getCell(5).getNumericCellValue()));
                        break;
                    }
                    case Cell.CELL_TYPE_STRING: {
                        entity.setGor(row.getCell(5).getStringCellValue());
                    }
                }

                Cell sepPressureCell = row.getCell(6, Row.CREATE_NULL_AS_BLANK);
                switch (sepPressureCell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK: {
                        entity.setSPressure(String.valueOf(""));
                        break;
                    }
                    case Cell.CELL_TYPE_NUMERIC: {
                        entity.setSPressure(String.valueOf(row.getCell(6).getNumericCellValue()));
                        break;
                    }
                }

                Cell remarkCell = row.getCell(11, Row.CREATE_NULL_AS_BLANK);
                switch (remarkCell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK: {
                        entity.setRemarks(String.valueOf(""));
                        break;
                    }
                    case Cell.CELL_TYPE_STRING: {
                        entity.setRemarks(row.getCell(11).getStringCellValue());
                    }

                }
                wellTestEntities.add(entity);
            }
            break;

            case "W.C": {
                WaterCutEntity entity = new WaterCutEntity();
                entity.setWellName(sheet.getSheetName());
                double date = row.getCell(dateColumnIndex).getNumericCellValue();
                entity.setMeasurementDate(new java.sql.Date((long) (date - 25569) * 86400 * 1000));

                Cell wcCell = row.getCell(4, Row.CREATE_NULL_AS_BLANK);
                switch (wcCell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC: {
                        entity.setWaterCut(String.valueOf(row.getCell(4).getNumericCellValue()));
                        break;
                    }
                    case Cell.CELL_TYPE_BLANK: {
                        //means they entered it using merged cells. so, search for a value in the first cell.
                        String mergedCellValue = row.getCell(2).getStringCellValue();
                        entity.setWaterCut(mergedCellValue);
                        break;
                    }
                    case Cell.CELL_TYPE_STRING: {
                        entity.setWaterCut(row.getCell(2).getStringCellValue());
                    }
                }

                Cell remarkCell = row.getCell(11, Row.CREATE_NULL_AS_BLANK);
                switch (remarkCell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK: {
                        entity.setComments("");
                        break;
                    }
                    case Cell.CELL_TYPE_STRING: {
                        entity.setComments(row.getCell(11).getStringCellValue());
                    }
                }
                waterCutEntities.add(entity);
            }
            break;
            case "F.L": {
                FluidLevelEntity entity = new FluidLevelEntity();
                entity.setWellName(sheet.getSheetName());
                entity.setActionKey("Fluid Level Test");
                double date = row.getCell(dateColumnIndex).getNumericCellValue();
                entity.setMeasurementDate(new java.sql.Date((long) (date - 25569) * 86400 * 1000));

                Cell dflCell = row.getCell(7, Row.CREATE_NULL_AS_BLANK);
                switch (dflCell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC: {
                        entity.setDynamicFluidLevel(String.valueOf(row.getCell(7).getNumericCellValue()));
                        break;
                    }
                    case Cell.CELL_TYPE_BLANK: {
                        entity.setDynamicFluidLevel("");
                        break;
                    }
                    case Cell.CELL_TYPE_STRING: {
                        entity.setDynamicFluidLevel(row.getCell(7).getStringCellValue());
                        break;
                    }
                }

                Cell liq = row.getCell(8, Row.CREATE_NULL_AS_BLANK);
                switch (liq.getCellType()) {
                    case Cell.CELL_TYPE_BLANK: {
                        entity.setLiquidPercentage(String.valueOf(""));
                        break;
                    }
                    case Cell.CELL_TYPE_NUMERIC: {
                        entity.setLiquidPercentage(String.valueOf(row.getCell(8).getNumericCellValue()));

                        break;
                    }
                }

                Cell staticFL = row.getCell(9, Row.CREATE_NULL_AS_BLANK);
                switch (staticFL.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC: {
                        entity.setStaticFluidLevel(String.valueOf(row.getCell(9).getNumericCellValue()));
                        break;
                    }
                    case Cell.CELL_TYPE_BLANK: {
                        entity.setStaticFluidLevel("");
                        break;
                    }
                    case Cell.CELL_TYPE_STRING: {
                        entity.setStaticFluidLevel(row.getCell(9).getStringCellValue());
                        break;
                    }
                }

                Cell remarkCell = row.getCell(11, Row.CREATE_NULL_AS_BLANK);
                switch (remarkCell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK: {
                        entity.setRemarks("");
                        break;
                    }
                    case Cell.CELL_TYPE_STRING: {
                        entity.setRemarks(row.getCell(11).getStringCellValue());
                    }
                }

                fluidLevelEntities.add(entity);
            }
            break;
            default: {
//              String wcKeyValue = (row.getCell(4, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL).getStringCellValue());
//                if (wcKeyValue.length() >2) {
//                    //this means they stored it as a remark
//                    WellRemarksEntity e = new WellRemarksEntity();
//                    e.setActionDate(String.valueOf(row.getCell(0).getDateCellValue()));
//                    e.setActionKey("lab remark");
//                    e.setOperationalComment(row.getCell());
//                } else {
//
//                }
//                WaterCutEntity entity = new WaterCutEntity();
//                entity.setWellName(sheet.getSheetName());
//                entity.setMeasurementDate(String.valueOf(row.getCell(0).getDateCellValue()));
//                entity.setComments(row.getCell(11).getStringCellValue());
//
            }
            break;

        }
    }

    public SQLData manageDate(double date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MMMM.yyyy HH:mm");
        System.out.println("---> "
                + sdf.format(new java.sql.Date(
                (long) ((date - 25569) * 86400 * 1000))));
        return (SQLData) new Date((long) (date - 25569) * 86400 * 1000);
    }


}
