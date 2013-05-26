package com.kahl.twitterwall.service;

import java.util.List;

import com.kahl.twitterwall.entity.Tweet;

public interface RegexCheckService {

    public boolean isActive();

    public void setActive(boolean active);

    public boolean matchesAutoBlockRegex(Tweet tweet);

    public void setRegexList(List<String> regexList);

    public List<String> getRegexList();

}
