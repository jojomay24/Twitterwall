package com.kahl.twitterwall.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
    public Tweet getTweetByTwitterId(long id) {
        Session session = sessionFactory.openSession();
        Query q = session.createQuery("From Tweet where tweetId = " + id);

        Tweet result = (Tweet) q.uniqueResult();
        session.close();

        log.debug("FOUND " + result.toString());
        return result;
    }

    @Override
    public List<Tweet> getTweetsByFilter(long minTweetId, int ackState) {
        List<Tweet> resultList = new ArrayList<Tweet>();
        Session session = sessionFactory.openSession();

        Criteria c = session.createCriteria(Tweet.class);
        if (minTweetId != -1) {
            c.add(Restrictions.ge("tweetId", minTweetId));
        }
        if (ackState != -1) {
            c.add(Restrictions.eq("ackState", ackState));
        }
        resultList = c.list();
        log.info("FOUND " + resultList.size() + " elements");
        session.close();

        return resultList;
    }

}
