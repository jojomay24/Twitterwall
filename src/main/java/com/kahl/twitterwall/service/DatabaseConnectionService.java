package com.kahl.twitterwall.service;

import com.kahl.twitterwall.entity.Tweet;

public interface DatabaseConnectionService {

    public void saveTweetToDb(Tweet tweet);

}
