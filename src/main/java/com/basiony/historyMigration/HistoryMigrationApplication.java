package com.basiony.historyMigration;

import com.basiony.historyMigration.service.serviceImpl.BodySectionReadingServiceImpl;
import com.basiony.historyMigration.service.serviceImpl.MetaSectionReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class HistoryMigrationApplication implements CommandLineRunner {
    private final MetaSectionReadingService metaSectionReadingService;
    private final BodySectionReadingServiceImpl bodySectionReadingService;

    public static void main(String[] args) {
        SpringApplication.run(HistoryMigrationApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("the Directory Has the following Excel sheets:");
        metaSectionReadingService.getWorkBooksInDirectory();
        metaSectionReadingService.loopOverSheets();
        bodySectionReadingService.readBodySection();
        System.out.println("successfully managed to read data for : " + metaSectionReadingService.totalNumberOfSheets + "sheets");
    }


}
