package com.shashank.dynamicscheduler.service;

import com.shashank.dynamicscheduler.dao.JobRepo;
import com.shashank.dynamicscheduler.model.ScheduleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static com.shashank.dynamicscheduler.constants.JobConstants.FILE_GEN_JOB;
import static com.shashank.dynamicscheduler.constants.JobConstants.SCHEDULE_KEY;

@Service
@Slf4j
public class SchedulerService implements SchedulingConfigurer {

    final JobRepo repo;

    final FileGenService fileGenService;

    final TaskScheduler poolScheduler;

    ScheduledTaskRegistrar scheduledTaskRegistrar;

    Map<String, ScheduledFuture> futureMap = new HashMap<>();

    public SchedulerService(JobRepo repo, FileGenService fileGenService, TaskScheduler poolScheduler) {
        this.repo = repo;
        this.fileGenService = fileGenService;
        this.poolScheduler = poolScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (scheduledTaskRegistrar == null) {
            scheduledTaskRegistrar = taskRegistrar;
        }
        if (taskRegistrar.getScheduler() == null) {
            taskRegistrar.setScheduler(poolScheduler);
        }

        //initializing the job on startup. Can be changed as needed.
        initScheduling();
    }

    public boolean initScheduling(){
        if (futureMap.containsKey(FILE_GEN_JOB)) {
            return false;
        }

        ScheduledFuture future = scheduledTaskRegistrar.getScheduler().schedule(() -> fileGenService.generateXML(), t -> {
            CronTrigger crontrigger = new CronTrigger(repo.findById(SCHEDULE_KEY).get().getScheduleValue());
            return crontrigger.nextExecutionTime(t);
        });
        futureMap.put(FILE_GEN_JOB, future);
        return true;
    }

    public boolean updateScheduleJob(){
        if (!futureMap.containsKey(FILE_GEN_JOB)) {
            return false;
        }
        ScheduledFuture future = futureMap.get(FILE_GEN_JOB);
        future.cancel(true);
        futureMap.remove(FILE_GEN_JOB);
        initScheduling();
        return true;
    }

    //initializing the db on startup. Can be changed as needed.
    @PostConstruct
    public void initializeDatabase() {
        ScheduleConfig config = ScheduleConfig.builder()
                .scheduleKey(SCHEDULE_KEY).scheduleValue("*/10 * * * * *").build();
        repo.save(config);
    }

}
