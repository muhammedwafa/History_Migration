//package com.basiony.historyMigration.utils;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import java.util.TreeSet;
//
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.*;
//
//import static org.apache.tomcat.util.bcel.classfile.ElementValue.STRING;
//
//public class PoiCopySheet {
//    private Set<CellRangeAddressWrapper> mergedRegions_ = new TreeSet<CellRangeAddressWrapper>();
//    private Sheet srcSheet_;
//    private Sheet dstSheet_;
//    private Workbook srcWorkbook_;
//    private Workbook dstWorkbook_;
//    private CellStyle[] srcToDstStyle_;
//
//    private PoiCopySheet(Sheet srcSheet, Sheet dstSheet) {
//        srcSheet_ = srcSheet;
//        dstSheet_ = dstSheet;
//        srcWorkbook_ = srcSheet.getWorkbook();
//        dstWorkbook_ = dstSheet.getWorkbook();
//    }
//
//    public static void copySheet(Sheet srcSheet, Sheet dstSheet) {
//        PoiCopySheet poiCopySheet = new PoiCopySheet(srcSheet, dstSheet);
//        poiCopySheet.copySheetInst();
//    }
//
//    private CellStyle getDstStyleFromSrcStyle(CellStyle srcStyle) {
//        return srcToDstStyle_[srcStyle.getIndex()];
//    }
//
//    private void copySheetInst() {
//        mapCellStyles();
//        copySheetSettings();
//        int maxColumnNum = 0;
//        for (int i = srcSheet_.getFirstRowNum(); i <= srcSheet_.getLastRowNum(); i++) {
//            Row srcRow = srcSheet_.getRow(i);
//            Row destRow = dstSheet_.createRow(i);
//            if (srcRow != null) {
//                copyRow(srcRow, destRow);
//                if (srcRow.getLastCellNum() > maxColumnNum) {
//                    maxColumnNum = srcRow.getLastCellNum();
//                }
//            }
//        }
//        for (int i = 0; i <= maxColumnNum; i++) {
//            dstSheet_.setColumnWidth(i, srcSheet_.getColumnWidth(i));
//            CellStyle srcColStyle = srcSheet_.getColumnStyle(i);
//            if (srcColStyle != null) {
//                dstSheet_.setDefaultColumnStyle(i, getDstStyleFromSrcStyle(srcColStyle));
//            }
//        }
//    }
//
//    private void mapCellStyles() {
//        int totalSrcCellStyles = srcWorkbook_.getNumCellStyles();
//        srcToDstStyle_ = new CellStyle[totalSrcCellStyles];
//        for (int srcStyleIdx = 0; srcStyleIdx < totalSrcCellStyles; ++srcStyleIdx) {
//            CellStyle srcCellStyle = srcWorkbook_.getCellStyleAt(srcStyleIdx);
//            srcToDstStyle_[srcStyleIdx] = getCellStyle(srcCellStyle);
//        }
//    }
//
//    private void copyRow(Row srcRow,
//                         Row dstRow) {
//        if (srcRow.isFormatted()) {
//            CellStyle srcRowStyle = srcRow.getRowStyle();
//            CellStyle dstRowStyle = getDstStyleFromSrcStyle(srcRowStyle);
//            dstRow.setRowStyle(dstRowStyle);
//        }
//
//        short dh = srcSheet_.getDefaultRowHeight();
//        if (srcRow.getHeight() != dh) {
//            dstRow.setHeight(srcRow.getHeight());
//        }
//
//        int j = srcRow.getFirstCellNum();
//        if (j < 0) {
//            j = 0;
//        }
//        for (; j <= srcRow.getLastCellNum(); j++) {
//            Cell srcCell = srcRow.getCell(j); // ancienne cell
//            Cell dstCell = dstRow.getCell(j); // new cell
//            if (srcCell != null) {
//                if (dstCell == null) {
//                    dstCell = dstRow.createCell(j);
//                }
//
//                copyCell(srcCell, dstCell);
//                CellRangeAddress mergedRegion = getMergedRegion(srcSheet_, srcRow.getRowNum(),
//                        (short) srcCell.getColumnIndex());
//
//                if (mergedRegion != null) {
//                    CellRangeAddress dstMergedRegion = new CellRangeAddress(mergedRegion.getFirstRow(),
//                            mergedRegion.getLastRow(), mergedRegion.getFirstColumn(), mergedRegion.getLastColumn());
//                    CellRangeAddressWrapper wrapper = new CellRangeAddressWrapper(dstMergedRegion);
//                    if (isdstMergedRegion(wrapper)) {
//                        mergedRegions_.add(wrapper);
//                        dstSheet_.addMergedRegion(wrapper.range);
//                    }
//                }
//            }
//        }
//    }
//
//    private static CellRangeAddress getMergedRegion(Sheet sheet, int rowNum, short cellNum) {
//        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
//            CellRangeAddress merged = sheet.getMergedRegion(i);
//            if (merged.isInRange(rowNum, cellNum)) {
//                return merged;
//            }
//        }
//        return null;
//    }
//
//    private boolean isdstMergedRegion(CellRangeAddressWrapper dstMergedRegion) {
//        return !mergedRegions_.contains(dstMergedRegion);
//    }
//
//    private void copyCell(Cell srcCell, Cell dstCell) {
//        if (srcWorkbook_ == dstWorkbook_) {
//            dstCell.setCellStyle(srcCell.getCellStyle());
//        } else {
//            CellStyle dstCellStyle = getDstStyleFromSrcStyle(srcCell.getCellStyle());
//            dstCell.setCellStyle(dstCellStyle);
//        }
//        switch (srcCell.getCellType()) {
//            case STRING:
//                dstCell.setCellValue(srcCell.getStringCellValue());
//                break;
//            case NUMERIC:
//                dstCell.setCellValue(srcCell.getNumericCellValue());
//                break;
//            case BLANK:
//                dstCell.setBlank();
//                break;
//            case BOOLEAN:
//                dstCell.setCellValue(srcCell.getBooleanCellValue());
//                break;
//            case ERROR:
//                dstCell.setCellErrorValue(srcCell.getErrorCellValue());
//                break;
//            case FORMULA:
//                dstCell.setCellFormula(srcCell.getCellFormula());
//                break;
//            default:
//                break;
//        }
//    }
//
//    private CellStyle getSameCellStyle(CellStyle srcCellStyle) {
//        DataFormat dstDataFormat = dstWorkbook_.createDataFormat();
//        String srcDataFormatString = srcCellStyle.getDataFormatString();
//        short expectedSrcDataFormat = dstDataFormat.getFormat(srcDataFormatString);
//
//        int totalSrcCellStyles = dstWorkbook_.getNumCellStyles();
//        for (int dstStyleIdx = 0; dstStyleIdx < totalSrcCellStyles; ++dstStyleIdx) {
//            CellStyle currentCellStyle = dstWorkbook_.getCellStyleAt(dstStyleIdx);
//
//            if (cellStylesEquals(currentCellStyle,      // CellStyle leftStyle,
//                    dstWorkbook_,          // Workbook leftWorkbook,
//                    srcCellStyle,          // CellStyle rightStyle,
//                    srcWorkbook_,          // Workbook rightWorkbook,
//                    expectedSrcDataFormat) // short rightDataFormat,
//            ) {
//                return currentCellStyle;
//            }
//        }
//        return null;
//    }
//
//    private static boolean cellStylesEquals(CellStyle leftStyle,
//                                            Workbook leftWorkbook,
//                                            CellStyle rightStyle,
//                                            Workbook rightWorkbook,
//                                            short rightDataFormat) {
//        if (leftStyle.getAlignment() != rightStyle.getAlignment()) {
//            return false;
//        }
//        if (leftStyle.getHidden() != rightStyle.getHidden()) {
//            return false;
//        }
//        if (leftStyle.getLocked() != rightStyle.getLocked()) {
//            return false;
//        }
//        if (leftStyle.getWrapText() != rightStyle.getWrapText()) {
//            return false;
//        }
//        if (leftStyle.getBorderBottom() != rightStyle.getBorderBottom()) {
//            return false;
//        }
//        if (leftStyle.getBorderLeft() != rightStyle.getBorderLeft()) {
//            return false;
//        }
//        if (leftStyle.getBorderRight() != rightStyle.getBorderRight()) {
//            return false;
//        }
//        if (leftStyle.getBorderTop() != rightStyle.getBorderTop()) {
//            return false;
//        }
//        if (leftStyle.getBottomBorderColor() != rightStyle.getBottomBorderColor()) {
//            return false;
//        }
//        if (leftStyle.getFillBackgroundColor() != rightStyle.getFillBackgroundColor()) {
//            return false;
//        }
//        if (leftStyle.getFillForegroundColor() != rightStyle.getFillForegroundColor()) {
//            return false;
//        }
//        if (leftStyle.getFillPattern() != rightStyle.getFillPattern()) {
//            return false;
//        }
//        if (leftStyle.getIndention() != rightStyle.getIndention()) {
//            return false;
//        }
//        if (leftStyle.getLeftBorderColor() != rightStyle.getLeftBorderColor()) {
//            return false;
//        }
//        if (leftStyle.getRightBorderColor() != rightStyle.getRightBorderColor()) {
//            return false;
//        }
//        if (leftStyle.getRotation() != rightStyle.getRotation()) {
//            return false;
//        }
//        if (leftStyle.getTopBorderColor() != rightStyle.getTopBorderColor()) {
//            return false;
//        }
//        if (leftStyle.getVerticalAlignment() != rightStyle.getVerticalAlignment()) {
//            return false;
//        }
//
//        Font leftFont = leftWorkbook.getFontAt(leftStyle.getFontIndexAsInt());
//        Font rightFont = rightWorkbook.getFontAt(rightStyle.getFontIndexAsInt());
//
//        if (leftFont.getBold() != rightFont.getBold()) {
//            return false;
//        }
//        if (leftFont.getColor() != rightFont.getColor()) {
//            return false;
//        }
//        if (leftFont.getFontHeight() != rightFont.getFontHeight()) {
//            return false;
//        }
//        if (!(leftFont.getFontName().equals(rightFont.getFontName()))) {
//            return false;
//        }
//        if (leftFont.getItalic() != rightFont.getItalic()) {
//            return false;
//        }
//        if (leftFont.getStrikeout() != rightFont.getStrikeout()) {
//            return false;
//        }
//        if (leftFont.getTypeOffset() != rightFont.getTypeOffset()) {
//            return false;
//        }
//        if (leftFont.getUnderline() != rightFont.getUnderline()) {
//            return false;
//        }
//        if (leftFont.getCharSet() != rightFont.getCharSet()) {
//            return false;
//        }
//
//        var leftDataFormatString = leftStyle.getDataFormatString();
//        var rightDataFormatString = rightStyle.getDataFormatString();
//        if (!leftDataFormatString.equals(rightDataFormatString)) {
//            if (rightDataFormat != leftStyle.getDataFormat()) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private CellStyle getCellStyle(CellStyle srcCellStyle) {
//        CellStyle dstCellStyle = getSameCellStyle(srcCellStyle);
//        if (dstCellStyle == null) {
//            dstCellStyle = cloneCellStyle(srcCellStyle);
//            // Using our custom cloneCellStyle as it will reuse existing font if it already exists
//            // while cloneStyleFrom() will create a new font even if it already exists.
//            //dstCellStyle = dstWorkbook_.createCellStyle();
//            //dstCellStyle.cloneStyleFrom(srcCellStyle);
//        }
//
//        return dstCellStyle;
//    }
//
//    // This is preferred over cloneStyleFrom() because it will reuse an existing font
//    // instead of always creating a new one.
//    private CellStyle cloneCellStyle(CellStyle srcCellStyle) {
//        // Create a new cell style
//        Font srcFont = srcWorkbook_.getFontAt(srcCellStyle.getFontIndexAsInt());
//        // Find a existing font corresponding to avoid to create a
//        // new one
//        Font dstFont = dstWorkbook_
//                .findFont(srcFont.getBold(), srcFont.getColor(), srcFont.getFontHeight(),
//                        srcFont.getFontName(), srcFont.getItalic(), srcFont.getStrikeout(),
//                        srcFont.getTypeOffset(), srcFont.getUnderline());
//        if (dstFont == null) {
//            dstFont = dstWorkbook_.createFont();
//            dstFont.setBold(srcFont.getBold());
//            dstFont.setColor(srcFont.getColor());
//            dstFont.setFontHeight(srcFont.getFontHeight());
//            dstFont.setFontName(srcFont.getFontName());
//            dstFont.setItalic(srcFont.getItalic());
//            dstFont.setStrikeout(srcFont.getStrikeout());
//            dstFont.setTypeOffset(srcFont.getTypeOffset());
//            dstFont.setUnderline(srcFont.getUnderline());
//            dstFont.setCharSet(srcFont.getCharSet());
//        }
//
//        CellStyle dstCellStyle = dstWorkbook_.createCellStyle();
//        dstCellStyle.setFont(dstFont);
//
//        DataFormat dstDataFormat = dstWorkbook_.createDataFormat();
//        String srcDataFormatString = srcCellStyle.getDataFormatString();
//        short dstFormat = dstDataFormat.getFormat(srcDataFormatString);
//        dstCellStyle.setDataFormat(dstFormat);
//
//        dstCellStyle.setAlignment(srcCellStyle.getAlignment());
//        dstCellStyle.setHidden(srcCellStyle.getHidden());
//        dstCellStyle.setLocked(srcCellStyle.getLocked());
//        dstCellStyle.setWrapText(srcCellStyle.getWrapText());
//        dstCellStyle.setBorderBottom(srcCellStyle.getBorderBottom());
//        dstCellStyle.setBorderLeft(srcCellStyle.getBorderLeft());
//        dstCellStyle.setBorderRight(srcCellStyle.getBorderRight());
//        dstCellStyle.setBorderTop(srcCellStyle.getBorderTop());
//        dstCellStyle.setBottomBorderColor(srcCellStyle.getBottomBorderColor());
//        dstCellStyle.setFillBackgroundColor(srcCellStyle.getFillBackgroundColor());
//        dstCellStyle.setFillForegroundColor(srcCellStyle.getFillForegroundColor());
//        dstCellStyle.setFillPattern(srcCellStyle.getFillPattern());
//        dstCellStyle.setIndention(srcCellStyle.getIndention());
//        dstCellStyle.setLeftBorderColor(srcCellStyle.getLeftBorderColor());
//        dstCellStyle.setRightBorderColor(srcCellStyle.getRightBorderColor());
//        dstCellStyle.setRotation(srcCellStyle.getRotation());
//        dstCellStyle.setTopBorderColor(srcCellStyle.getTopBorderColor());
//        dstCellStyle.setVerticalAlignment(srcCellStyle.getVerticalAlignment());
//        return dstCellStyle;
//    }
//
//    private void copySheetSettings() {
//        dstSheet_.setAutobreaks(srcSheet_.getAutobreaks());
//        dstSheet_.setDefaultColumnWidth(srcSheet_.getDefaultColumnWidth());
//        dstSheet_.setDefaultRowHeight(srcSheet_.getDefaultRowHeight());
//        dstSheet_.setDefaultRowHeightInPoints(srcSheet_.getDefaultRowHeightInPoints());
//        dstSheet_.setDisplayGuts(srcSheet_.getDisplayGuts());
//        dstSheet_.setFitToPage(srcSheet_.getFitToPage());
//
//        dstSheet_.setForceFormulaRecalculation(srcSheet_.getForceFormulaRecalculation());
//
//        PrintSetup srcSheet_PrintSetup = srcSheet_.getPrintSetup();
//        PrintSetup dstSheet_PrintSetup = dstSheet_.getPrintSetup();
//
//        dstSheet_PrintSetup.setPaperSize(srcSheet_PrintSetup.getPaperSize());
//        dstSheet_PrintSetup.setScale(srcSheet_PrintSetup.getScale());
//        dstSheet_PrintSetup.setPageStart(srcSheet_PrintSetup.getPageStart());
//        dstSheet_PrintSetup.setFitWidth(srcSheet_PrintSetup.getFitWidth());
//        dstSheet_PrintSetup.setFitHeight(srcSheet_PrintSetup.getFitHeight());
//        dstSheet_PrintSetup.setLeftToRight(srcSheet_PrintSetup.getLeftToRight());
//        dstSheet_PrintSetup.setLandscape(srcSheet_PrintSetup.getLandscape());
//        dstSheet_PrintSetup.setValidSettings(srcSheet_PrintSetup.getValidSettings());
//        dstSheet_PrintSetup.setNoColor(srcSheet_PrintSetup.getNoColor());
//        dstSheet_PrintSetup.setDraft(srcSheet_PrintSetup.getDraft());
//        dstSheet_PrintSetup.setNotes(srcSheet_PrintSetup.getNotes());
//        dstSheet_PrintSetup.setNoOrientation(srcSheet_PrintSetup.getNoOrientation());
//        dstSheet_PrintSetup.setUsePage(srcSheet_PrintSetup.getUsePage());
//        dstSheet_PrintSetup.setHResolution(srcSheet_PrintSetup.getHResolution());
//        dstSheet_PrintSetup.setVResolution(srcSheet_PrintSetup.getVResolution());
//        dstSheet_PrintSetup.setHeaderMargin(srcSheet_PrintSetup.getHeaderMargin());
//        dstSheet_PrintSetup.setFooterMargin(srcSheet_PrintSetup.getFooterMargin());
//        dstSheet_PrintSetup.setCopies(srcSheet_PrintSetup.getCopies());
//
//        Header srcSheet_Header = srcSheet_.getHeader();
//        Header dstSheet_Header = dstSheet_.getHeader();
//        dstSheet_Header.setCenter(srcSheet_Header.getCenter());
//        dstSheet_Header.setLeft(srcSheet_Header.getLeft());
//        dstSheet_Header.setRight(srcSheet_Header.getRight());
//
//        Footer srcSheet_Footer = srcSheet_.getFooter();
//        Footer dstSheet_Footer = dstSheet_.getFooter();
//        dstSheet_Footer.setCenter(srcSheet_Footer.getCenter());
//        dstSheet_Footer.setLeft(srcSheet_Footer.getLeft());
//        dstSheet_Footer.setRight(srcSheet_Footer.getRight());
//
//        dstSheet_.setHorizontallyCenter(srcSheet_.getHorizontallyCenter());
//        dstSheet_.setMargin(Sheet.LeftMargin, srcSheet_.getMargin(Sheet.LeftMargin));
//        dstSheet_.setMargin(Sheet.RightMargin, srcSheet_.getMargin(Sheet.RightMargin));
//        dstSheet_.setMargin(Sheet.TopMargin, srcSheet_.getMargin(Sheet.TopMargin));
//        dstSheet_.setMargin(Sheet.BottomMargin, srcSheet_.getMargin(Sheet.BottomMargin));
//
//        dstSheet_.setPrintGridlines(srcSheet_.isPrintGridlines());
//        dstSheet_.setRowSumsBelow(srcSheet_.getRowSumsBelow());
//        dstSheet_.setRowSumsRight(srcSheet_.getRowSumsRight());
//        dstSheet_.setVerticallyCenter(srcSheet_.getVerticallyCenter());
//        dstSheet_.setDisplayFormulas(srcSheet_.isDisplayFormulas());
//        dstSheet_.setDisplayGridlines(srcSheet_.isDisplayGridlines());
//        dstSheet_.setDisplayRowColHeadings(srcSheet_.isDisplayRowColHeadings());
//        dstSheet_.setDisplayZeros(srcSheet_.isDisplayZeros());
//        dstSheet_.setPrintGridlines(srcSheet_.isPrintGridlines());
//        dstSheet_.setRightToLeft(srcSheet_.isRightToLeft());
//        dstSheet_.setZoom(100);
//        //copyPrintTitle(dstSheet_, srcSheet_);
//    }
//}