package com.kahl.twitterwall.dao;

import java.util.List;

import com.kahl.twitterwall.entity.Tweet;

public interface TwitterwallDao {

    public void saveTweetToDb(Tweet t);

    public Tweet getTweetByTwitterId(long id);

    public List<Tweet> getTweetsByFilter(long minTweetId, int ackState);

}
