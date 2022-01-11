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
    private final MetaSectionReadingService metaSectionReadingService = null;
    private final BodySectionReadingServiceImpl bodySectionReadingService;
//
//    public HistoryMigrationApplication(BodySectionReadingServiceImpl service){
//        this.bodySectionReadingService = service;
//    }
    public static void main(String[] args) {
        SpringApplication.run(HistoryMigrationApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        metaSectionReadingService.loopOverSheets();
        bodySectionReadingService.readBodySection();
    }


}
