package com.basiony.historyMigration.service.serviceImpl;

import com.basiony.historyMigration.buisnessModels.WaterCut;
import com.basiony.historyMigration.entityModels.FluidLevelEntity;
import com.basiony.historyMigration.entityModels.WaterCutEntity;
import com.basiony.historyMigration.entityModels.WellTestEntity;
import com.basiony.historyMigration.repo.FluidLevelRepository;
import com.basiony.historyMigration.repo.WaterCutRepository;
import com.basiony.historyMigration.repo.WellMetaDataRepository;
import com.basiony.historyMigration.repo.WellTestRepository;
import com.basiony.historyMigration.service.BodySectionReadingService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

@Service
public class BodySectionReadingServiceImpl implements BodySectionReadingService {


    // here uploadFolder contains the well history data
    final String filePath = "src/main/resources/static/mergedCells.xlsx";
    private final int dateColumnIndex = 0;
    int actualLastRowIndex = 7;
    XSSFWorkbook workbook;
    XSSFSheet sheet;

    ArrayList<WaterCutEntity> waterCutEntities = new ArrayList<>();
    ArrayList<WellTestEntity> wellTestEntities = new ArrayList<>();
    ArrayList<FluidLevelEntity> fluidLevelEntities = new ArrayList<>();
    WellTestRepository wellTestRepository;
    WaterCutRepository waterCutRepository;
    FluidLevelRepository fluidLevelRepository;


    @Autowired
    public BodySectionReadingServiceImpl(WellTestRepository wellTestRepository, WaterCutRepository waterCutRepository, FluidLevelRepository fluidLevelRepository) {
        this.wellTestRepository = wellTestRepository;
        this.waterCutRepository = waterCutRepository;
        this.fluidLevelRepository = fluidLevelRepository;
    }

    @Override
    public void workingWithDates() {

        try (InputStream file = new FileInputStream(filePath)) {

            //Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(file);

            //Get desired sheets from the workbook
            for (Sheet currentWorkSheet : workbook) {
                sheet = (XSSFSheet) currentWorkSheet;
                /***
                 * getting the exact number of rows based on the actual data entered the key column
                 * because in some cells, specially the net cell values, they have a pre-defined equation
                 * which makes it difficult to get the actual number of rows.
                 */
                int actualRowCount = getActualRowCount();
                System.out.println("The method return is : " + actualRowCount);

                for (int i = 6; i < actualLastRowIndex - 1; i++) {
                    XSSFRow currentRow = sheet.getRow(i);
                    XSSFCell dateCell = currentRow.getCell(dateColumnIndex);
                    int keyColumnIndex = 1;
                    XSSFCell keyCell = currentRow.getCell(keyColumnIndex);
                    manageMergedCells(sheet, dateCell);
                    getHistoryObjects(currentWorkSheet, currentRow, keyCell);
                }
            }
            System.out.println(actualLastRowIndex);
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

    /**
     * Calculate the actual number of rows based on the key colum.
     */
    private int getActualRowCount() {
        for (int i = 6; i < actualLastRowIndex; i++) {
            XSSFRow row = sheet.getRow(i);
            Cell keyCell = row.getCell(1);
            String keyCellValue = getCellValue(row, 1);
            if (keyCellValue != null && keyCellValue.length() > 0) { // the key cell has a value
                // System.out.println(keyCell.getStringCellValue());
                actualLastRowIndex++;
                if (keyCell.getStringCellValue() == null) {
                    break;
                }
            }
        }

        return actualLastRowIndex - 1; // should add -1 because Excel is 0 based indexed.
    }


    /**
     * unmerging the merged cells and populating the null cells with the corresponding values.
     * since merged cells contains the value at the upper left cell and the rest are considered blank.
     * so we are working on getting the previous cell value and populating the next empty ones with the retrieved value.
     */

    private void manageMergedCells(Sheet sheet, XSSFCell cell) {

        // looping over the actual rows and handling the merged cells.
        if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
            // System.out.println("found blank cell @ index " + cell.getRowIndex());
            Row previousRow = sheet.getRow(cell.getRowIndex() - 1);
            Cell previousCell = previousRow.getCell(dateColumnIndex);
            cell.setCellValue(previousCell.getDateCellValue());
            // System.out.println("Updated cell @ index  " + cell.getRowIndex() + "with value " + previousCell.getDateCellValue());
        }

    }

    /***
     * This method should loop over the key column and based on the key value it will create one of the following objects:
     * well test object, fluid level object , water cut measurements object and/or well remarks object.
     */
    private void getHistoryObjects(Sheet sheet, XSSFRow row, XSSFCell keyCell) {

        String key = keyCell.getStringCellValue().toUpperCase();
        System.out.println(key);
        String wcKey = new String("W.C");
        String wellTestKey = new String("TEST");
        String fluidLevel = new String("F.L");

        switch (key) {
            case "W.C": {
                WaterCutEntity entity = new WaterCutEntity();
                entity.setWellName(sheet.getSheetName());
                entity.setMeasurementDate(String.valueOf(row.getCell(0).getDateCellValue()));
                entity.setWaterCut(row.getCell(4, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL).getNumericCellValue());
                entity.setComments(row.getCell(11).getStringCellValue());
                waterCutEntities.add(entity);
                break;
            }
            case "TEST": {
                WellTestEntity entity = new WellTestEntity();
                entity.setWellName(sheet.getSheetName());
                entity.setMeasurementDate(row.getCell(dateColumnIndex).getDateCellValue().toString());
                entity.setActionKey("Well Test");
                entity.setGross(row.getCell(2).getNumericCellValue());
                entity.setWc(row.getCell(4).getNumericCellValue());
                entity.setNet(entity.getGross() * (1 - (entity.getWc())/100));
                entity.setGor(row.getCell(5).getStringCellValue());
                entity.setSPressure(String.valueOf(row.getCell(6).getNumericCellValue()));
                entity.setRemarks(row.getCell(11).getStringCellValue());
                wellTestEntities.add(entity);
            }
            System.out.println("found well test");
            break;
            case "F.L":
                FluidLevelEntity entity = new FluidLevelEntity();
                entity.setWellName(sheet.getSheetName());
                entity.setActionKey("Fluid Level Test");
                entity.setMeasurementDate(String.valueOf(row.getCell(0).getDateCellValue()));
                entity.setDynamicFluidLevel(String.valueOf(row.getCell(7).getNumericCellValue()));
                entity.setLiquidPercentage(String.valueOf(row.getCell(8).getNumericCellValue()));
                entity.setStaticFluidLevel(row.getCell(9).getStringCellValue());
                entity.setRemarks(row.getCell(11).getStringCellValue());
                fluidLevelEntities.add(entity);
                break;
        }
    }

    private int determineRowCount() {
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        DataFormatter formatter = new DataFormatter(true);

        int lastRowIndex = -1;
        if (sheet.getPhysicalNumberOfRows() > 0) {
            // getLastRowNum() actually returns an index, not a row number
            lastRowIndex = sheet.getLastRowNum();

            // now, start at end of spreadsheet and work our way backwards until we find a row having data
            for (; lastRowIndex >= 0; lastRowIndex--) {
                Row row = sheet.getRow(lastRowIndex);
                if (!isRowEmpty(row)) {
                    break;
                }
            }
        }
        return lastRowIndex;
    }

    /**
     * Determine whether a row is effectively completely empty - i.e. all cells either contain an empty string or nothing.
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        int cellCount = row.getLastCellNum() + 1;
        for (int i = 0; i < cellCount; i++) {
            String cellValue = getCellValue(row, i);
            if (cellValue != null && cellValue.length() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the effective value of a cell, formatted according to the formatting of the cell.
     * If the cell contains a formula, it is evaluated first, then the result is formatted
     */
    private String getCellValue(Row row, int columnIndex) {
        String cellValue = "";
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            // no data in this cell
            cellValue = null;
        } else if (cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
            // cell has a value, so format it into a string
            cell.setCellType(CellType.STRING);
            cellValue = cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cellValue = String.valueOf(cell.getDateCellValue());
        }
        return cellValue;
    }
}
