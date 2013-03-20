package com.kahl.twitterwall.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public List<Tweet> getTweetsByFilter(long minTweetId, int ackState) {
//        if (minTweetId == -1 && ackState == -1) {
//            return new ArrayList<Tweet>();
//        }

        return dao.getTweetsByFilter(minTweetId, ackState);
    }

    @Override
    public Tweet getTweetFromDb(long tweetDbId) {
        if (tweetDbId == 0) {
            return null;
        } else {
            return dao.getTweetByTwitterId(tweetDbId);
        }
    }

    @Override
    public void saveTweetToDb(Tweet tweet) {
        dao.saveTweetToDb(tweet);
    }

    @Override
    public void ackTweetsByTweetId(Map<Long, Integer> ackMap) {
        dao.ackTweetsByTweetId(ackMap);

    }

}
