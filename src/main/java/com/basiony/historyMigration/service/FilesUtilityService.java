package com.basiony.historyMigration.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;

public interface FilesUtilityService {
    void getWorkBooksInDirectory() throws IOException, InvalidFormatException;

}
