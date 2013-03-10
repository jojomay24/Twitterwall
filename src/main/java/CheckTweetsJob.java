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
 
public class CheckTweetsJob implements Job {
 
   private Logger log = Logger.getLogger(CheckTweetsJob.class);
   public static String SEARCH_STRING = "#test";
//   public static String SEARCH_STRING = "from:ErnaTestibus";
   
   public static long lastSinceId = -1;
   
   public static boolean isRunning = false; //TODO Thread safe machen!
   
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
                   log.info("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                   
                   long tweetId = tweet.getId();
                   maxId = (maxId < tweetId ) ? tweetId : maxId;
                   
                   log.info("Tweet To String: " + tweet.toString() + "\n ----------------------------------------------------");
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