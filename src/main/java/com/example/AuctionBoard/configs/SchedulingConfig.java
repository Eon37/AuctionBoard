package com.example.AuctionBoard.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulingConfig {
    public static Long NOTIFY_AFTER_DAYS = 1L;

    @Value("${notify.after.days:1}")
    public void setNotifyAfterDays(Long value){
        SchedulingConfig.NOTIFY_AFTER_DAYS = value;
    }

    //todo better something persistent
    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
