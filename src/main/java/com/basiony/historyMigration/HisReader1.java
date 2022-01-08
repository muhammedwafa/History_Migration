package com.basiony.historyMigration;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class HisReader1 {


    public static void readExcelFile() {
        final String filePath = "src/main/resources/static/aman wells.xlsx";
        try {
            /***
             * this method makes our program format independent, and it works for both types of files - .xls and .xlsx.
             */
            Workbook workbook = WorkbookFactory.create(new File(filePath));

            System.out.println("workbook has " + workbook.getNumberOfSheets() + " sheets");

            //   getting the sheet of the opened workbook at specific index.
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet currentSheet = sheetIterator.next();
                System.out.println("data for sheet at Index: " + workbook.getSheetIndex(currentSheet.getSheetName())
                        + " with Name : " + currentSheet.getSheetName());
                for (Row row : currentSheet) {
                    for (Cell cell : row) {
                        printCellValue(cell);
                    }
                }

                saveDataInMemory(currentSheet);

            }
            /**
             * //using lambda expressions
             *             workbook.forEach(sh -> {
             *                         System.out.println("using Lambda expression");
             *                         System.out.println(sh.getSheetName());
             *                     }
             *             );
             */

        } catch (FileNotFoundException exception) {
            System.out.println(exception.getMessage());
            System.out.println("File Was Not Found.");
        } catch (IOException | InvalidFormatException e) { //catch statement for XSSFWorkbook.
            e.printStackTrace();
        }
    }

    //method for printing cell value based on cell type.
    public static void printCellValue(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                System.out.println(cell.getBooleanCellValue() +"\t" );
                break;
            case STRING:
                System.out.println(cell.getRichStringCellValue().getString() +"\t");
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    System.out.println(cell.getDateCellValue() +"\t");
                } else {
                    System.out.println(cell.getNumericCellValue() +"\t");
                }
                break;
            case FORMULA:
                System.out.println(cell.getNumericCellValue() +"\n");
                break;
            default:
                System.out.println();
        }

    }

    //method for saving data to map.
    public static void saveDataInMemory(Sheet sheet) {
        //   getting the data
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<>());
            for (Cell cell : row) {
                switch (cell.getCellTypeEnum()) {
                    case STRING:
                        String stringCellValue = cell.getStringCellValue();
                        data.get(i).add(stringCellValue);
                        System.out.println(data.get(i).toString());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            System.out.print(cell.getDateCellValue());
                            System.out.println(cell.getDateCellValue());
                        } else {
                            // getting the numeric cell value, casting it to string and saving it.
                            data.get(i).add(String.valueOf(cell.getNumericCellValue()));
                            System.out.println(data.get(i).toString());
                        }
                        break;
                    case FORMULA:
                        // getting the numeric cell value, casting it to string and saving it.
                        data.get(i).add(String.valueOf(cell.getNumericCellValue()));
                        System.out.println(data.get(i).toString());
                    case BOOLEAN:
                        System.out.print(cell.getBooleanCellValue());
                        break;
                    case BLANK:
                        System.out.println(" ");
                        break;
                    default:
                        System.out.println("this is the default behavior");
                }
            }
            i++;
        }

        System.out.println("the size is Map is : " + data.size());
        System.out.println("The content of the Map is: .");
        data.forEach((key, value)-> {
            System.out.println(key + ":" + value);
        });
    }
}
