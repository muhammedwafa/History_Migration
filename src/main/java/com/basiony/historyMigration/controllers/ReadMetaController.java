package com.basiony.historyMigration.controllers;

import com.basiony.historyMigration.buisnessModels.WellMettaData;
import com.basiony.historyMigration.service.serviceImpl.MetaSectionReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController()
@RequestMapping(path = "/api/metaData")
public class ReadMetaController {

    private final MetaSectionReadingService historyReadingService;

    @Autowired
    public ReadMetaController(MetaSectionReadingService historyReadingService) {
        this.historyReadingService = historyReadingService;
    }

    @GetMapping(path = "/findAll")
    private List<WellMettaData> readAll() {
        System.out.println("called the get method successfully");
        return historyReadingService.getAll();
    }

}
