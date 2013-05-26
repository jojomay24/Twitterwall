package com.kahl.twitterwall.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kahl.twitterwall.entity.Tweet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class RegexCheckServiceImplTest {

    @Autowired
    private RegexCheckService service;

    @Test
    public void testMatchesAutoBlockRegex() {
//        RegexCheckService service = new RegexCheckServiceImpl();

        boolean[] expectedResults = { false,    true,    true,    true };
        String[] tweetMessages = {      "",     "Hallo", "hALLo", "http:\\gmx.de" };
        String[] regexExpressions = {   "'.*'", "hallo", "hallo", "http.*de" };

        for (int i = 0; i < expectedResults.length; i++) {
            List<String> testRegexList = Arrays.asList(regexExpressions[i]);
            service.setRegexList(testRegexList);
            Tweet testTweet = new Tweet();
            testTweet.setText(tweetMessages[i]);
            assertEquals("Test (text,regex): " + tweetMessages[i] + "," + regexExpressions[i], expectedResults[i], service.matchesAutoBlockRegex(testTweet));
        }



    }

}
