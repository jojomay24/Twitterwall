package com.kahl.twitterwall.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.kahl.twitterwall.entity.Tweet;

public interface TwitterwallDao {

    public void saveTweetToDb(Tweet t);

    public void updateTweet(Tweet t);

    public List<Tweet> getTweetsByFilter(long minTweetId, int ackState, int maxResults, int offset);

    public List<Tweet> getOpenTweetsOlderThan(Date date);

    public int getTotalNrOfTweetsByFilter(long minTweetId, int ackState);

    public Tweet getTweetByTwitterId(String id);

    void ackTweetsByTweetId(Map<String, Integer> ackMap);


}
