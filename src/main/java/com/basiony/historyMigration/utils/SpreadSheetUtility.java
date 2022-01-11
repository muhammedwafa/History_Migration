package com.basiony.historyMigration.utils;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;


/***
 * This utility class should contatin methods to help maneging the Excel spreadsheet
 */
public class SpreadSheetUtility {
    private XSSFSheet sheet;
    private XSSFRow row;
    private XSSFCell cell;
    private final int basicRowIndex = 6; // my data always start at row index = 6;
    private FormulaEvaluator evaluator;
    private DataFormatter formatter;

    public SpreadSheetUtility() {

    }

    //get the actual used row numbers
    public int actualRowNumbers(XSSFSheet sheet) {

        this.evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        this.formatter = new DataFormatter(true);

        int lastRowIndex = -1;
        if (sheet.getPhysicalNumberOfRows() > 0) {
            // getLastRowNum() actually returns an index, not a row number
            lastRowIndex = sheet.getLastRowNum();

            // now, start at end of spreadsheet and work our way backwards until we find a row having data
            for (; lastRowIndex >= 0; lastRowIndex--) {
                row = sheet.getRow(lastRowIndex);
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
    private boolean isRowEmpty(XSSFRow row) {
        if (row == null) {
            return true;
        }

        int cellCount = row.getLastCellNum() + 1;
        for (int i = 0; i < 2; i++) { // looping ove the first two cells only.
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
    public String getCellValue(XSSFRow row, int columnIndex) {
        String cellValue;
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cellValue = null; // no data in this cell

        } else {
            if (cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
                cellValue = this.formatter.formatCellValue(cell);// cell has a value, so format it into a string
            } else {
                cellValue = this.formatter.formatCellValue(cell, this.evaluator); // cell has a formula, so evaluate it

            }
        }
        return cellValue;
    }


    public void manageMergedCells(Sheet sheet, XSSFCell cell) {

        // looping over the actual rows and handling the merged cells. i.e. updating the current null values with the previous value.
        if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
            Row previousRow = sheet.getRow(cell.getRowIndex() - 1);
            Cell previousCell = previousRow.getCell(1);
            previousCell.setCellType(CellType.NUMERIC);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(previousCell.getNumericCellValue());
            System.out.println("sheet Name: " + cell.getSheet().getSheetName() +" Row index: " +  cell.getRowIndex() + 1 );
        }

    }

    public void formatRange(XSSFSheet sheet,int start, int end) {
        for (int i = start; i <end ; i++) {
            XSSFRow row = sheet.getRow(i);
            for (Cell cell: row) {
                cell.setCellType(CellType.NUMERIC);
            }
        }
    }

    public void formatCellAsString(Cell cell){
        cell.setCellType(CellType.STRING);
    }
    public void formatCellAsNumber(Cell cell){
        cell.setCellType(CellType.NUMERIC);
    }
}
