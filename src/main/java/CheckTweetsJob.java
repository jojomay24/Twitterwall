import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.kahl.twitterwall.entity.Tweet;
import com.kahl.twitterwall.entity.TwitterUser;
import com.kahl.twitterwall.service.DatabaseConnectionService;
import com.kahl.twitterwall.service.DatabaseConnectionServiceMysqlImpl;

public class CheckTweetsJob implements Job {

   private Logger log = Logger.getLogger(CheckTweetsJob.class);
   public static String SEARCH_STRING = "#test";
//   public static String SEARCH_STRING = "from:ErnaTestibus";

   public static long lastSinceId = -1;
   public static boolean isRunning = false; //TODO Thread safe machen!

   public DatabaseConnectionService dbService = new DatabaseConnectionServiceMysqlImpl();

   private Query buildQuery() {
       Query q = new Query(SEARCH_STRING);

       if (!(JobScheduler.EARLIEST_DATE.isEmpty())) {
           q.setSince(JobScheduler.EARLIEST_DATE);
       }

       if ( lastSinceId != -1) {
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

           QueryResult result;
           long maxId = -1;
           do {
               result = twitter.search(query);
               List<Status> tweets = result.getTweets();
               for (Status tweet : tweets) {
                   log.info("Creating User " + tweet.getUser().getScreenName() + " - " + tweet.getUser().getProfileImageURL());
                   TwitterUser twitterUser = new TwitterUser();
                   twitterUser.setName(tweet.getUser().getScreenName());
                   twitterUser.setProfileImageUrl(tweet.getUser().getProfileImageURL());

                   log.info("Creating Tweet: " + tweet.getText());
                   Tweet tweetObj = new Tweet();
                   tweetObj.setAckState(0);
                   tweetObj.setText(tweet.getText());
                   tweetObj.setCreatedAt(tweet.getCreatedAt());
                   tweetObj.setTweetId(tweet.getId());
                   tweetObj.setTwitterUser(twitterUser);

                   maxId = (maxId < tweetObj.getTweetId() ) ? tweetObj.getTweetId() : maxId;

                   dbService.saveTweetToDb(tweetObj);
               }
           } while ((query = result.nextQuery()) != null);
           lastSinceId = ( maxId != -1 ) ? maxId : lastSinceId;


           log.info("Finished!");
       } catch (TwitterException te) {
           log.warn("Failed to search tweets: " + te.getMessage());
       }

       isRunning = false;
       return;
   }

}