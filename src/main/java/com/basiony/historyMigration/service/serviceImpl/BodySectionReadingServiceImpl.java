package com.basiony.historyMigration.service.serviceImpl;

import com.basiony.historyMigration.service.BodySectionReadingService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

@Service
public class BodySectionReadingServiceImpl implements BodySectionReadingService {


    // here uploadFolder contains the well history data
    final String filePath = "src/main/resources/static/mergedCells.xlsx";
    int dateColumnIndex = 0;
    int actualLastRowIndex = 7;
    XSSFWorkbook workbook;
    XSSFSheet sheet;

    @Override
    public void workingWithDates() {

        try (InputStream file = new FileInputStream(filePath)) {

            //Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(file);

            //Get desired sheets from the workbook
            for (Sheet workSheet : workbook) {
                sheet = (XSSFSheet) workSheet;

                System.out.println("sheet name is: " + sheet.getSheetName());
                System.out.println(sheet.getLastRowNum());
                System.out.println(sheet.getLeftCol());
                System.out.println(sheet.getPhysicalNumberOfRows());

                /***
                 * getting the exact number of rows based on the actual data entered the key column
                 * because in some cells, specially the net cell values, they have a pre-defined equation
                 * which makes it difficult to get the actual number of rows.
                 */
                for (int i = 6; i < actualLastRowIndex; i++) {
                    XSSFRow row = sheet.getRow(i);
                    Cell keyCell = row.getCell(1);
                    String keyCellValue = getCellValue(row, 1);
                    if (keyCellValue != null && keyCellValue.length() > 0) { // the key cell has a value
                        System.out.println(keyCell.getStringCellValue());
                        actualLastRowIndex++;
                        System.out.println("the sheet has active rows." + actualLastRowIndex);
                        if (keyCell.getStringCellValue() == null) {
                            break;
                        }
                    }
//                    switch (keyCell.getCellTypeEnum()) {
//                        case STRING: {
//                            /***
//                             * working on populating the merged cell values.
//                             */
//
//                            XSSFCell cell = row.getCell(dateColumnIndex);
//                            System.out.println(cell.getRowIndex() + ": " + cell.getDateCellValue());
//                            if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
//                                System.out.println("found blank cell @ " + cell.getRowIndex());
//
//                                //get the previous row data
//                                int previousRowIndex = row.getRowNum() - 1;
//                                cell.setCellValue(sheet.getRow(previousRowIndex).
//                                        getCell(dateColumnIndex).getDateCellValue());
//                                System.out.println("cell value after update is: w" + cell.getDateCellValue());
//                            }
//                        }
//                        break;
//                        case _NONE:
//                            System.out.println("found nothing.");
//                    }
                }
                System.out.println(actualLastRowIndex);

            }

        } catch (
                IOException e) {
            e.printStackTrace();
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
     * If the cell contains a formula, it is evaluated first, then the result is formatted.
     *
     * @param row         the row
     * @param columnIndex the cell's column index
     * @return the cell's value
     */
    private String getCellValue(Row row, int columnIndex) {
        String cellValue;
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            // no data in this cell
            cellValue = null;
        } else {
            if (cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
                // cell has a value, so format it into a string
                cell.setCellType(CellType.STRING);
                cellValue = cell.getStringCellValue();
            } else {
                // cell has a formula, so evaluate it
//                cellValue = this.formatter.formatCellValue(cell, this.evaluator);
                System.out.println("doing nothing");
                cellValue = "cellValue";
            }
        }
        return cellValue;
    }
}
