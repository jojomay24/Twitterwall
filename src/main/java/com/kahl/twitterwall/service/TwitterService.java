package com.kahl.twitterwall.service;

import java.util.List;

import com.kahl.twitterwall.entity.Tweet;

public interface TwitterService {

    public void saveTweetToDb(Tweet tweet);

    public Tweet getTweetFromDb(long tweetDbId);

    public List<Tweet> getTweetsByFilter(long minTweetId, int ackState);

}
