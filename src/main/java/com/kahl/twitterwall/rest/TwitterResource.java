package com.kahl.twitterwall.rest;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.kahl.twitterwall.Twitterwall;
import com.kahl.twitterwall.entity.Tweet;
import com.kahl.twitterwall.service.TwitterService;

@Path("/twitter")
public class TwitterResource {

    public TwitterResource() {
        twitterService = (TwitterService) Twitterwall.ctx.getBean("twitterServiceImpl");
    }

    private TwitterService twitterService;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response getTweetJson(
            @DefaultValue("0") @QueryParam("tweetId") long tweetId)
    {
        Tweet t = twitterService.getTweetFromDb(tweetId);
        if (t == null) {
        } else {
        }
        return Response
                .status(200)
                .entity(t).build();
    }

//    @PUT
//    @Path("/ack")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public void ackTweets()
//
//
    @GET
//    @Produces(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tweet")
    public Response getTweet(
            @DefaultValue("0") @QueryParam("tweetId") long tweetId)
    {
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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tweets")
    public Response getTweets(
            @DefaultValue("-1") @QueryParam("minTweetId") long minTweetId,
            @DefaultValue("-1") @QueryParam("ackState") int ackState)
    {
        String resultString = "";
        List<Tweet> l = twitterService.getTweetsByFilter(minTweetId,ackState);
        if (l.isEmpty()) {
            resultString = "Leider konnte kein passender Tweet gefunden werdenn";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Found " + l.size() + " Tweets: \n");
            for (Tweet tweet : l) {
                sb.append(tweet.toString());
                sb.append("\n");
            }
            resultString= sb.toString();
        }
        return Response
                .status(200)
                .entity(l).build();
    }


}