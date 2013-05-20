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
import com.kahl.twitterwall.entity.TweetsTransferObject;
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
            @DefaultValue("-1") @QueryParam("ackState") int ackState,
            @DefaultValue("0") @QueryParam("offset") int offset)
    {
        log.info("getTweets called: minTweetId: " + minTweetId + ",ackState:" + ackState + ", offset: " + offset);
        int maxResults=25;

        TweetsTransferObject tto = new TweetsTransferObject();
        int totalFoundNr = 0;
        List<Tweet> l = twitterService.getTweetsByFilter(minTweetId, ackState, maxResults, offset);
        if (!(l.isEmpty())) {
                totalFoundNr = twitterService.getTotalNrOfTweetsByFilter(minTweetId, ackState);
        }
         tto.tweets = l;
         tto.totalFoundNr = totalFoundNr;
         tto.ackState = ackState;
         tto.offset = offset;

         return Response
         .status(200)
         .entity(tto).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/minAutoAckAge")
    public Response getMinAutoAckAge()
    {
        log.info("getMinAutoAckAge called.Returning:" + Twitterwall.MIN_TWEET_AGE_FOR_AUTO_ACK);

        return Response
                .status(200)
                .entity(Twitterwall.MIN_TWEET_AGE_FOR_AUTO_ACK).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/minAutoAckAge")
    public void setMinAutoAckAge(Integer newMinTweetAgeForAutoAck)
    {
        log.info("setMinAutoAckAge called with newMinTweetAgeForAutoAck: " + newMinTweetAgeForAutoAck);
        Twitterwall.MIN_TWEET_AGE_FOR_AUTO_ACK = newMinTweetAgeForAutoAck;

        return;
    }

}