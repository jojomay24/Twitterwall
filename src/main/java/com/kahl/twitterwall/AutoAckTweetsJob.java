package com.kahl.twitterwall;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.kahl.twitterwall.service.TwitterService;

@Component
public class AutoAckTweetsJob implements Job {

    private Logger log = Logger.getLogger(AutoAckTweetsJob.class);

    @Override
    public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
        log.info("Running AutoAckTweetsJob with MIN_TWEET_AGE_FOR_AUTO_ACK: " + Twitterwall.MIN_TWEET_AGE_FOR_AUTO_ACK);
        TwitterService twitterService = (TwitterService) Twitterwall.ctx.getBean("twitterServiceImpl");
        twitterService.ackOpenTweetsOlderThan(Twitterwall.MIN_TWEET_AGE_FOR_AUTO_ACK);
        return;
    }

}