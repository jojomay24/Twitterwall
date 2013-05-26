package com.kahl.twitterwall.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahl.twitterwall.Twitterwall;
import com.kahl.twitterwall.dao.TwitterwallDao;
import com.kahl.twitterwall.entity.Tweet;

@Service
public class TwitterServiceImpl implements TwitterService {

    private Logger log = Logger.getLogger(TwitterServiceImpl.class);

    @Autowired
    private TwitterwallDao dao;

    @Override
    public List<Tweet> getTweetsByFilter(long minTweetId, int ackState, int maxResults, int offset) {
//        if (minTweetId == -1 && ackState == -1) {
//            return new ArrayList<Tweet>();
//        }

        return dao.getTweetsByFilter(minTweetId, ackState, maxResults, offset);
    }

    @Override
    public int getTotalNrOfTweetsByFilter(long minTweetId, int ackState) {
        return dao.getTotalNrOfTweetsByFilter(minTweetId, ackState);
    }

    @Override
    public Tweet getTweetFromDb(String tweetDbId) {
        if (tweetDbId.equals("0")) {
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
    public void ackTweetsByTweetId(Map<String, Integer> ackMap) {
        dao.ackTweetsByTweetId(ackMap);

    }

    @Override
    public void ackOpenTweetsOlderThan(int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, - seconds);
        List<Tweet> tweets = dao.getOpenTweetsOlderThan(cal.getTime());

        for (Tweet tweet : tweets) {
            tweet.setAckState(Twitterwall.STATE_ACKED);
            dao.updateTweet(tweet);
            log.debug("autoacked tweet " + tweet);
        }
        log.info("autoacked " + tweets.size() + " tweets");
    }

}
