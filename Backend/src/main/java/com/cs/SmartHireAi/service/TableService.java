package com.cs.SmartHireAi.service;

import com.cs.SmartHireAi.repository.TableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TableService {
    @Autowired
    TableRepo tableRepo;
    public void test1(){
        tableRepo.test();
    }
}
