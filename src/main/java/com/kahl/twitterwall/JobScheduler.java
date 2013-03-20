package com.kahl.twitterwall;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import com.kahl.twitterwall.rest.GrizzlyServer;

/**
 * @author Alexander Kahl
 * @since March 2013
 * @version 1.0.0
 *
 */
@Service
public class JobScheduler {

    private static Logger log = Logger.getLogger(JobScheduler.class);

    private Scheduler sch;
    private JobDetail job;
    private Trigger trigger;

    public JobScheduler() {
        // specify the job' s details..
        job = JobBuilder.newJob(CheckTweetsJob.class)
                .withIdentity("checkTweetsJob")
                .build();

        // specify the running period of the job
        trigger = TriggerBuilder.newTrigger()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(Twitterwall.TWEETS_SEARCH_INTERVAL)
                        .repeatForever())
                .build();
    }

    public void startTwitterScheduler() {
        SchedulerFactory schFactory = new StdSchedulerFactory();
        try {
            sch = schFactory.getScheduler();
            sch.start();
            sch.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("Failed starting the JobScheduler: \n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopTwitterScheduler() {
        try {
            sch.shutdown();
        } catch (SchedulerException e) {
            log.error("Failed stopping the JobScheduler: \n" + e.getMessage());
        }
    }

}
