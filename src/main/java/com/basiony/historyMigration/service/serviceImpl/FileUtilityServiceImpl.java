package com.basiony.historyMigration.service.serviceImpl;

import com.basiony.historyMigration.service.FilesUtilityService;
import com.basiony.historyMigration.utils.SpreadSheetUtility;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileUtilityServiceImpl implements FilesUtilityService {
    private int totalNumberOfSheets = 0;

    public int getTotalNumberOfSheets() {
        return totalNumberOfSheets;
    }


    @Override
    public void getWorkBooksInDirectory() throws IOException, InvalidFormatException {
        FileFilter filter = new FileUtilityServiceImpl.ExcelFileFilter();
        File directory = new File("src/main/resources/static");
        File[] files = directory.listFiles(filter);
        assert files != null;
        for (File file : files) {
            System.out.println("File Name: " + file.getName());
            System.out.println("counting the number of sheets ......");
            XSSFWorkbook currentWorkBook = new XSSFWorkbook(file);
            System.out.println("contains " + currentWorkBook.getNumberOfSheets() + " sheets");
            totalNumberOfSheets = totalNumberOfSheets + currentWorkBook.getNumberOfSheets();


            //separating wells based on the lift type.
            for (Sheet currentSheet : currentWorkBook) {
                //get the first row of the first cell.
                XSSFCell nameCell = (XSSFCell) currentSheet.getRow(0).getCell(0);
                if (nameCell.getStringCellValue().contains("ESP")) {

                    //create a new workbook and add the sheets to it.
                    try (FileOutputStream fos = new FileOutputStream(new File("src/main/resources/static/esp wells.xlsx"))){
                        Workbook workbook = new XSSFWorkbook();
                        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(currentSheet.getSheetName());
                        
                        Row row = sheet.createRow(0);
                        Cell cell = row.createCell(0);
                        cell.setCellValue("Hello Excel!");
                        workbook.write(fos);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                System.out.println(nameCell.getStringCellValue());
            }
        }
    }


    // creating a file filter to get the Excel files only.
    public static class ExcelFileFilter implements java.io.FileFilter {
        @Override
        public boolean accept(File file) {
            return file != null &&
                    file.isFile() &&
                    file.canRead() &&
                    (file.getName().endsWith("xls")
                            || file.getName().endsWith("xlsx"));
        }

    }
}
