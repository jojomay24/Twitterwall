package com.kahl.twitterwall;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.kahl.twitterwall.entity.Tweet;
import com.kahl.twitterwall.entity.TwitterUser;
import com.kahl.twitterwall.service.RegexCheckService;
import com.kahl.twitterwall.service.TwitterService;

@Component
public class CheckTweetsJob implements Job {

    private Logger log = Logger.getLogger(CheckTweetsJob.class);

    public static long lastSinceId = -1;
    public static boolean isRunning = false; // TODO Thread safe machen!

    private Query buildQuery() {
        Query q = new Query(Twitterwall.SEARCH_STRING);

        if (!(Twitterwall.TWEETS_SEARCH_EARLIEST_DATE.isEmpty())) {
            q.setSince(Twitterwall.TWEETS_SEARCH_EARLIEST_DATE);
        }

        if (lastSinceId != -1) {
            q.setSinceId(lastSinceId);
        }

        return q;
    }

    @Override
    public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
        if (isRunning) {
            log.debug("Other Job has not finished yet - skipping this run");
            return;
        } else {
            isRunning = true;
        }
        Twitter twitter = new TwitterFactory().getInstance();
        try {
            Query query = buildQuery();
            log.debug("Query: " + query.toString());
            log.info("Grabbing tweets from twitter");
            TwitterService ts = (TwitterService) Twitterwall.ctx.getBean("twitterServiceImpl");
            RegexCheckService regexService = (RegexCheckService) Twitterwall.ctx.getBean("regexCheckServiceImpl");

            QueryResult result;
            long maxId = -1;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    Tweet tweetObj = createTweetEntity(tweet);

                    if (regexService.isActive() && regexService.matchesAutoBlockRegex(tweetObj)) {
                        tweetObj.setAckState(Twitterwall.STATE_BLOCKED);
                        log.info("Blocked tweet because of regexBlocking! Tweet: " + tweetObj.getText());
                    }

                    long tweetIdAsLong = Long.parseLong(tweetObj.getTweetId());
                    maxId = (maxId < tweetIdAsLong) ? tweetIdAsLong : maxId;

                    // Instanzvariable kann aufgrund von Erzeugung der Objekte
                    // nicht verwendet werden!
                    log.info("Saving Tweet!");
                    ts.saveTweetToDb(tweetObj);
                }
            } while ((query = result.nextQuery()) != null);
            lastSinceId = (maxId != -1) ? maxId : lastSinceId;

            log.info("Finished!");
        } catch (TwitterException te) {
            log.warn("Failed to search tweets: " + te.getMessage());
        }

        isRunning = false;
        return;
    }

    private Tweet createTweetEntity(Status tweet) {
        log.debug("Creating User " + tweet.getUser().getScreenName() + " - " + tweet.getUser().getProfileImageURL());
        TwitterUser twitterUser = new TwitterUser();
        twitterUser.setName(tweet.getUser().getScreenName());
        twitterUser.setProfileImageUrl(tweet.getUser().getProfileImageURL());

        log.debug("Creating Tweet: " + tweet.getText());
        Tweet tweetObj = new Tweet();
        tweetObj.setAckState(0);
        tweetObj.setText(tweet.getText());
        tweetObj.setCreatedAt(tweet.getCreatedAt());
        tweetObj.setTweetId("" + tweet.getId());
        tweetObj.setTwitterUser(twitterUser);
        return tweetObj;
    }

}