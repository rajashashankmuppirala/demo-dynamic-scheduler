package com.shashank.dynamicscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoDynamicSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoDynamicSchedulerApplication.class, args);
    }

}
