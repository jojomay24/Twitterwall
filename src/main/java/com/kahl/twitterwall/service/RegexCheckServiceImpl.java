package com.kahl.twitterwall.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.kahl.twitterwall.Twitterwall;
import com.kahl.twitterwall.entity.Tweet;

@Service
public class RegexCheckServiceImpl implements RegexCheckService {

    private Logger log = Logger.getLogger(RegexCheckServiceImpl.class);

    private List<String> regexExpressions = new ArrayList<String>();

    @Override
    public boolean matchesAutoBlockRegex(Tweet tweet) {
        for (String regex : regexExpressions) {
            boolean isMatching = tweet.getText().toLowerCase().matches(regex);
            if (isMatching) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isActive() {
        return Twitterwall.REGEX_CHECK_ACTIVE;
    }

    @Override
    public void setActive(boolean active) {
        Twitterwall.REGEX_CHECK_ACTIVE = active;

        return;
    }

    @Override
    public void setRegexList(List<String> regexList) {
        regexExpressions = regexList;
    }

    @Override
    public List<String> getRegexList() {
        return regexExpressions;
    }


}
