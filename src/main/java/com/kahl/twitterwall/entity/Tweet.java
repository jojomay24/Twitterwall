package com.kahl.twitterwall.entity;

public class Tweet {

    private java.util.Date createdAt;
    private long tweetId;
    private int ackState = 0;
    private TwitterUser twitterUser;

    private String text;
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public java.util.Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }
    public long getTweetId() {
        return tweetId;
    }
    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }
    public int getAckState() {
        return ackState;
    }
    public void setAckState(int ackState) {
        this.ackState = ackState;
    }
    public TwitterUser getTwitterUser() {
        return twitterUser;
    }
    public void setTwitterUser(TwitterUser twitterUser) {
        this.twitterUser = twitterUser;
    }

}
