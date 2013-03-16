package com.kahl.twitterwall.service;

import com.kahl.twitterwall.entity.Tweet;

public interface TwitterService {

    public void saveTweetToDb(Tweet tweet);

    public String getTweetFromDb();

}
