package com.kahl.twitterwall.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


//@XmlRootElement
@Entity
@Table(name = "Tweet")
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private java.util.Date createdAt;
    private long tweetId;
    private int ackState = 0;
    private String text;

    @ManyToOne
    @JoinColumn(name="twitterUserId")
    private TwitterUser twitterUser;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

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

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append((twitterUser == null) ? "[Could not find Author]" : "[" + twitterUser.getName() + "]:");
        s.append("TweetId: " + getTweetId() + " | ");
        s.append("Created At: " + createdAt + " | ");
        s.append("Ack-State: " +  ackState + " | ");
        s.append("Text: " + text);

        return s.toString();
    }

}
