package com.kahl.twitterwall.service;

import java.util.List;
import java.util.Map;

import com.kahl.twitterwall.entity.Tweet;

public interface TwitterService {

    public void saveTweetToDb(Tweet tweet);

    public Tweet getTweetFromDb(long tweetDbId);

    public List<Tweet> getTweetsByFilter(long minTweetId, int ackState);

    public void ackTweetsByTweetId(Map<Long, Integer> ackMap);

}
