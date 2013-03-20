package com.kahl.twitterwall;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.kahl.twitterwall.rest.GrizzlyServer;

@Service
public class Twitterwall {

    public static String SEARCH_STRING = "#test";

    /** nr of seconds between querying Twitter for new results */
    public final static int TWEETS_SEARCH_INTERVAL = 15;

    /** Date should be formatted as YYYY-MM-DD */
    public final static String TWEETS_SEARCH_EARLIEST_DATE = "2013-03-10";

    public static boolean GRAB_TWEETS = false;
    public static boolean START_WEB_SERVER = true;

    private Logger log = Logger.getLogger(Twitterwall.class);

    @Autowired
    public JobScheduler scheduler;

    public static ApplicationContext ctx;

    public static void main(String[] args) {
        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("spring.xml");
        Twitterwall.ctx = ctx;

        Twitterwall twitterW = (Twitterwall) ctx.getBean("twitterwall");
        twitterW.start();
    }

    public void start() {
        log.info("Initializing Spring Container");

        log.info("Starting Twitterwall:");
        if (GRAB_TWEETS) {
        }
        if (START_WEB_SERVER) {
            startGrizzlyServer();
        }
    }

    private void startGrizzlyServer() {
        Thread t = new Thread(new GrizzlyServer());
        t.start();
    }

}
