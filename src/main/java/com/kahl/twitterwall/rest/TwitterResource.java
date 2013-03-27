package com.kahl.twitterwall.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.kahl.twitterwall.Twitterwall;
import com.kahl.twitterwall.entity.Tweet;
import com.kahl.twitterwall.service.TwitterService;

@Path("/twitter")
public class TwitterResource {

    private Logger log = Logger.getLogger(TwitterResource.class);

    public TwitterResource() {
        twitterService = (TwitterService) Twitterwall.ctx.getBean("twitterServiceImpl");
    }

    private TwitterService twitterService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response getTweetJson(
            @DefaultValue("0") @QueryParam("tweetId") String tweetId)
    {
        Tweet t = twitterService.getTweetFromDb(tweetId);
        if (t == null) {
        } else {
        }
        return Response
                .status(200)
                .entity(t).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/test2")
    public Response getFooBar() {
        String json = "{\"foo\": \"bar\"}";
        return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
    }

    @PUT
    @Path("/ack")
    @Consumes(MediaType.APPLICATION_JSON)
    public void ackTweets(Map<String, Integer> ackMap) {
        log.info("ackTweets called: ackMap size: " + ackMap.size());

        twitterService.ackTweetsByTweetId(ackMap);
//        return Response.ok().build();
//                .header("Access-Control-Allow-Origin", "*")
//                .header("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS")
//                .header("Access-Control-Allow-Headers", "Content-Type")
        return;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tweet")
    public Response getTweet(
            @DefaultValue("0") @QueryParam("tweetId") String tweetId)
    {
        log.info("getTweet called: tweetId:" + tweetId);
        String resultString = "";
        Tweet t = twitterService.getTweetFromDb(tweetId);
        if (t == null) {
            resultString = "Leider konnte kein passender Tweet gefunden werden";
        } else {
            resultString = t.toString();
        }
        return Response
                .status(200)
                .entity(t).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/tweets")
    public Response getTweets(
            @DefaultValue("-1") @QueryParam("minTweetId") long minTweetId,
            @DefaultValue("-1") @QueryParam("ackState") int ackState)
    {
        log.info("getTweets called: minTweetId: " + minTweetId + ",ackState:" + ackState);
        String resultString = "";
        List<Tweet> l = twitterService.getTweetsByFilter(minTweetId, ackState);
        if (l.isEmpty()) {
            resultString = "Leider konnte kein passender Tweet gefunden werdenn";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Found " + l.size() + " Tweets: \n");
            for (Tweet tweet : l) {
                sb.append(tweet.toString());
                sb.append("\n");
            }
            resultString = sb.toString();
        }
         return Response
         .status(200)
         .entity(l).build();
    }

}