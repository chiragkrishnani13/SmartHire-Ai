package com.cs.SmartHireAi.controller;

import com.cs.SmartHireAi.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TableCotroller {
    @Autowired
    TableService tableService;
    @GetMapping("/table")
    public void test1(){
        tableService.test1();
    }
}
