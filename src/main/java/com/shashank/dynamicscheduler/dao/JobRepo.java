package com.shashank.dynamicscheduler.dao;

import com.shashank.dynamicscheduler.model.ScheduleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepo extends JpaRepository<ScheduleConfig, String> {
}
