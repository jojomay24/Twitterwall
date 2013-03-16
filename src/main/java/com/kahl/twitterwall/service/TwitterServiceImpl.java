package com.kahl.twitterwall.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahl.twitterwall.dao.TwitterwallDao;
import com.kahl.twitterwall.entity.Tweet;

@Service
public class TwitterServiceImpl implements TwitterService {

    private Logger log = Logger.getLogger(TwitterServiceImpl.class);

    @Autowired
    private TwitterwallDao dao;

    @Override
    public String getTweetFromDb() {
        return "It could work";
    }

    @Override
    public void saveTweetToDb(Tweet tweet) {
        log.warn("Calling dao.save");
        dao.saveTweetToDb(tweet);
    }

}

