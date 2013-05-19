package com.kahl.twitterwall.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kahl.twitterwall.entity.Tweet;
import com.kahl.twitterwall.entity.TwitterUser;

@Repository
public class TwitterwallDaoImpl implements TwitterwallDao {

    private Logger log = Logger.getLogger(TwitterwallDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveTweetToDb(Tweet tweet) {
        try {
            Session session = sessionFactory.openSession();
            /*
             * Ugly, probably I didn't really understand how this hibernate
             * mapping works
             */
            Query q = session.createQuery("From TwitterUser where name = :name and profileImageUrl = :profileImageUrl");
            q.setParameter("name", tweet.getTwitterUser().getName());
            q.setParameter("profileImageUrl", tweet.getTwitterUser().getProfileImageUrl());
            if (q.list().size() > 0) {
                TwitterUser tu = (TwitterUser) q.list().get(0);
                tweet.setId(tu.getId());
            } else {
                session.save(tweet.getTwitterUser());
            }
            log.info("Saving Tweet " + tweet);
            session.saveOrUpdate(tweet);
            session.close();
        } catch (Exception e) {
            log.error("Failed to persist Tweet " + tweet + " from author " + tweet.getTwitterUser() + ": \n"
                    + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateTweet(Tweet tweet) {
        try {
            Session session = sessionFactory.openSession();
            session.update(tweet);
            log.info("updated tweet " + tweet);
            session.flush();
            session.close();
        } catch (Exception e) {
            log.error("Failed to update Tweet " + tweet + " from author " + tweet.getTwitterUser() + ": \n"
                    + e.getMessage());
        }
    }

    @Override
    public Tweet getTweetByTwitterId(String id) {
        Session session = sessionFactory.openSession();
        Query q = session.createQuery("From Tweet where tweetId = " + id);

        Tweet result = (Tweet) q.uniqueResult();
        session.close();

        return result;
    }

    @Override
    public List<Tweet> getTweetsByFilter(long minTweetId, int ackState, int maxResults, int offset) {
        List<Tweet> resultList = new ArrayList<Tweet>();
        Session session = sessionFactory.openSession();

        Criteria c = session.createCriteria(Tweet.class);
        c.setMaxResults(maxResults);
        if (minTweetId != -1) {
            c.add(Restrictions.ge("tweetId", minTweetId));
        }
        if (ackState != -1) {
            c.add(Restrictions.eq("ackState", ackState));
        }
        c.addOrder( Order.desc("tweetId") );
        c.setFirstResult(offset);
        resultList = c.list();
        log.info("FOUND " + resultList.size() + " elements (maxResults: " + maxResults + ", offset: " + offset + ")");
        session.close();

        return resultList;
    }

    @Override
    public List<Tweet> getTweetsOlderThan(Date date) {
        List<Tweet> resultList = new ArrayList<Tweet>();
        Session session = sessionFactory.openSession();

        Criteria c = session.createCriteria(Tweet.class);
        c.setMaxResults(3);
        c.add(Restrictions.eq("ackState", 0));
        c.add(Restrictions.le("createdAt", date));

        resultList = c.list();

        log.debug("Found " + resultList.size() + " elements older than " + date);
        session.close();

        return resultList;
    }

    @Override
    public int getTotalNrOfTweetsByFilter(long minTweetId, int ackState) {
        Session session = sessionFactory.openSession();

        Query q = session.createQuery("Select count(*) From Tweet where tweetId >= :minTweetId and ackState = :ackState");
        q.setLong("minTweetId", minTweetId);
        q.setInteger("ackState", ackState);
        List<Long> resultList = q.list();
        int result = resultList.get(0).intValue();
        log.info("TOTALLY FOUND " + result + " elements");

        session.close();
        return result;
    }

    @Override
    public void ackTweetsByTweetId(Map<String, Integer> ackMap) {
        Session session = sessionFactory.openSession();

        for (Map.Entry<String, Integer> entry : ackMap.entrySet()) {
            Tweet t = getTweetByTwitterId(entry.getKey());
            if (t == null) {
                log.warn("Could not update ackState for Tweet with twitterId [" + entry.getKey() +"] : No Tweet found!");
                continue;
            }
            log.info("Setting ackState [ " + entry.getValue() + "] for tweetId [" + entry.getKey() + "]");
            t.setAckState(entry.getValue());
            session.saveOrUpdate(t);
            session.flush();
        }

        session.close();
    }

}
