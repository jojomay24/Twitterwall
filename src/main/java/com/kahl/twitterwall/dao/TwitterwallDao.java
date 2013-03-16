package com.kahl.twitterwall.dao;

import com.kahl.twitterwall.entity.Tweet;

public interface TwitterwallDao {

    public void saveTweetToDb(Tweet t);

}
