package com.kahl.twitterwall.dao;

import java.util.List;
import java.util.Map;

import com.kahl.twitterwall.entity.Tweet;

public interface TwitterwallDao {

    public void saveTweetToDb(Tweet t);

    public Tweet getTweetByTwitterId(long id);

    public List<Tweet> getTweetsByFilter(long minTweetId, int ackState);

    public void ackTweetsByTweetId(Map<Long, Integer> ackMap);

}
