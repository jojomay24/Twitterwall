package com.kahl.twitterwall;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.kahl.twitterwall.rest.GrizzlyServer;

@Service
public class Twitterwall {

    public static String SEARCH_STRING = "#fcb";

    /** nr of seconds between querying Twitter for new results */
    public final static int TWEETS_SEARCH_INTERVAL = 15;

    /** nr of seconds between auto ack tweets runs */
    public final static int AUTO_ACK_TWEETS_INTERVAL = 25;

    /** Date should be formatted as YYYY-MM-DD */
    public final static String TWEETS_SEARCH_EARLIEST_DATE = "2013-04-04";

    /** switches */
    public static boolean GRAB_TWEETS = false;
    public static boolean AUTOACK_TWEETS = true;
    public static boolean START_WEB_SERVER = true;
    public static boolean REGEX_CHECK_ACTIVE = false;


    public static int STATE_OPEN  = 0;
    public static int STATE_ACKED  = 1;
    public static int STATE_BLOCKED  = 2;

    public static int MIN_TWEET_AGE_FOR_AUTO_ACK = 3600 * 24 * 365;

    private Logger log = Logger.getLogger(Twitterwall.class);

    public GetTweetsJobScheduler getWeetsJobScheduler;
    public AutoAckTweetsJobScheduler autoAckTweetsJobScheduler;

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

        if (GRAB_TWEETS) {
            getWeetsJobScheduler = new GetTweetsJobScheduler();
            getWeetsJobScheduler.startTwitterScheduler();
        }
        if (AUTOACK_TWEETS) {
            autoAckTweetsJobScheduler = new AutoAckTweetsJobScheduler();
            autoAckTweetsJobScheduler.startAutoAckTweetsScheduler();
        }
        if (START_WEB_SERVER) {
            startGrizzlyServer();
        }
    }

    private void startGrizzlyServer() {
        try {
        Thread t = new Thread(new GrizzlyServer());
        t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
