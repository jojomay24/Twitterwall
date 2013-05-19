package com.kahl.twitterwall;

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

/**
 * @author Alexander Kahl
 * @since March 2013
 * @version 1.0.0
 *
 */
@Service
public class AutoAckTweetsJobScheduler {

    private static Logger log = Logger.getLogger(AutoAckTweetsJobScheduler.class);

    private Scheduler sch;
    private JobDetail job;
    private Trigger trigger;

    public AutoAckTweetsJobScheduler() {
        // specify the job' s details..
        job = JobBuilder.newJob(AutoAckTweetsJob.class)
                .withIdentity("autoAckTweetsJob")
                .build();

        // specify the running period of the job
        log.info("Creating AutoAckTweetsJobScheduler with interval " + Twitterwall.AUTO_ACK_TWEETS_INTERVAL + " seconds");
        trigger = TriggerBuilder.newTrigger()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(Twitterwall.AUTO_ACK_TWEETS_INTERVAL)
                        .repeatForever())
                .build();
    }

    public void startAutoAckTweetsScheduler() {
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

    public void stopAutoAckTweetsScheduler() {
        try {
            sch.shutdown();
        } catch (SchedulerException e) {
            log.error("Failed stopping the JobScheduler: \n" + e.getMessage());
        }
    }

}
