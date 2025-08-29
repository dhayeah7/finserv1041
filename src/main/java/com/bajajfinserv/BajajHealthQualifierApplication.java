package com.bajajfinserv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class BajajHealthQualifierApplication {

    @Autowired
    private QualifierService qualifierService;

    public static void main(String[] args) {
        SpringApplication.run(BajajHealthQualifierApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runQualifier() {
        try {
            qualifierService.executeQualifierProcess();
        } catch (Exception e) {
            System.err.println("Error executing qualifier process: " + e.getMessage());
            e.printStackTrace();
        }
    }
}