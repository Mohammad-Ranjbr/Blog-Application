package com.blog_application.config;

import com.blog_application.model.post.Post;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Configuration
public class QuartzConfig {


    @Bean
    public JobDetail postJobDetail() {
        return JobBuilder.newJob(PostJob.class)
                .withIdentity("postJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger postJobTrigger(JobDetail jobDetail, Post post, LocalDateTime scheduledTime) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .usingJobData("post", post)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .startAt(Date.from(scheduledTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();
    }

}
