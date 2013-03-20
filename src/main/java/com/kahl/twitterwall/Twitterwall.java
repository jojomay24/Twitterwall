package com.kahl.twitterwall;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.kahl.twitterwall.rest.GrizzlyServer;

@Service
public class Twitterwall {

    private Logger log = Logger.getLogger(Twitterwall.class);

    @Autowired
    public JobScheduler scheduler;

    public static ApplicationContext ctx;

    public static void main(String[] args) {
        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("spring.xml");
        Twitterwall.ctx = ctx;

//        String[] names = ctx.getBeanDefinitionNames();
//        for (String name : names) {
//            System.out.println("Found name: " + name);
//        }

        Twitterwall twitterW = (Twitterwall) ctx.getBean("twitterwall");
        twitterW.start();
    }

    public void start() {
        log.info("Initializing Spring Container");

        log.info("Starting Twitterwall");
//         scheduler.startTwitterScheduler();
         startGrizzlyServer();
    }

    private void startGrizzlyServer() {
        Thread t = new Thread(new GrizzlyServer());
        t.start();
    }

}
