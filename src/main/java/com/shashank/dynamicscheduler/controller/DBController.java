package com.shashank.dynamicscheduler.controller;

import com.shashank.dynamicscheduler.dao.JobRepo;
import com.shashank.dynamicscheduler.model.ScheduleConfig;
import com.shashank.dynamicscheduler.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.shashank.dynamicscheduler.constants.JobConstants.SCHEDULE_KEY;

@RestController
@Slf4j
public class DBController {

    final JobRepo repo;

    final SchedulerService schedulerService;

    public DBController(JobRepo repo, SchedulerService schedulerService) {
        this.repo = repo;
        this.schedulerService = schedulerService;
    }

    @PostMapping("/updateTrigger")
    public void updateTriggerExpression(){
        ScheduleConfig config = repo.findById(SCHEDULE_KEY).get();
        config.setScheduleValue("*/25 * * * * *");
        repo.save(config);
        log.info("database updated for expression-->" + config.getScheduleValue() );
        boolean status = schedulerService.updateScheduleJob();
        log.info("schedule for job updated-->"+ status);
    }
}
