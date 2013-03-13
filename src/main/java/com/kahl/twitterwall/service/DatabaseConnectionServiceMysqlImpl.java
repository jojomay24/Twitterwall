package com.kahl.twitterwall.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.kahl.twitterwall.entity.Tweet;
import com.kahl.twitterwall.entity.TwitterUser;

public class DatabaseConnectionServiceMysqlImpl implements DatabaseConnectionService {

    private Logger log = Logger.getLogger(DatabaseConnectionServiceMysqlImpl.class);

    public final String DB_CONNECTION = "jdbc:mysql://localhost/Twitterwall";
    public final String DB_USER = "Twitterwall";
    public final String DB_PASSWORD = "Twitterwall123";
    public final String DB_DRIVER = "com.mysql.jdbc.Driver";


    @Override
    public void saveTweetToDb(Tweet tweet) {
        log.info("Storing Tweet " + tweet.getTwitterUser().getName() + " - " + tweet.getText());
        try {
            writeToDb(tweet);
        } catch (SQLException e) {
            log.error("Error storing tweet to db.\n" + e.getMessage());
        }
    }

    private void writeToDb(Tweet tweet) throws SQLException {
        System.out.println("Writing Entry to DB: ");
        int twitterUserKey = getPrimaryKeyForTwitterUser(tweet.getTwitterUser());
        if (-1 == twitterUserKey)
        {
            System.out.println("Couldn't find TwitterUser, creating ...");
            twitterUserKey = writeTwitterUserToDb(tweet.getTwitterUser());
        }

        tweet.getTwitterUser().setId(twitterUserKey);
        System.out.println("Writing Tweet to DB");
        writeTweetToDb(tweet);
        System.out.println("Finished Writing");
      }


    private int getPrimaryKeyForTwitterUser(TwitterUser twitterUser) {
        Connection dbConnection = getDBConnection();
        PreparedStatement prepSt1 = null;
        int foundId = -1;

        String selectSQL = "SELECT id FROM TwitterUser WHERE name = ? and profileImageUrl = ?";
    //    String selectSQL = "SELECT id FROM TwitterUser WHERE name = ?";
        try {
            prepSt1 = dbConnection.prepareStatement(selectSQL);
            prepSt1.setString(1, twitterUser.getName());
            prepSt1.setString(2, twitterUser.getProfileImageUrl());
            ResultSet rs = prepSt1.executeQuery();
            while (rs.next()) {
                foundId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foundId;
    }

    private int writeTwitterUserToDb(TwitterUser twitterUser) {
        Connection dbConnection = null;
        PreparedStatement prepSt1 = null;
        int genKey = -1;

        String insertTwitterUserSQL = "INSERT INTO Twitterwall.TwitterUser"
                + "(name, profileImageUrl) VALUES (?,?)";

        try {
            dbConnection = getDBConnection();

            prepSt1 = dbConnection.prepareStatement(insertTwitterUserSQL,PreparedStatement.RETURN_GENERATED_KEYS);
            prepSt1.setString(1, twitterUser.getName());
            prepSt1.setString(2, twitterUser.getProfileImageUrl());
            prepSt1.executeUpdate();

            ResultSet rs = prepSt1.getGeneratedKeys();
            if (rs.next()) {
                genKey = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (prepSt1 != null) {
                try {
                    prepSt1.close();
                } catch (SQLException e) {
                }
            }
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException e) {
                }
            }
        }

        return genKey;
    }

    private void writeTweetToDb(Tweet tweet) {
        Connection dbConnection = null;
        PreparedStatement prepSt1 = null;

        String insertTweetSQL = "INSERT INTO Twitterwall.Tweet"
                + "(tweetId, text, createdAt, twitterUserId, ackState)"
                + "VALUES (?,?,?,?,?)";

        try {
            dbConnection = getDBConnection();
            dbConnection.setAutoCommit(false);

            prepSt1 = dbConnection.prepareStatement(insertTweetSQL);
            prepSt1.setLong(1, tweet.getTweetId());
            prepSt1.setString(2, tweet.getText());
            prepSt1.setTimestamp(3, new java.sql.Timestamp(tweet.getCreatedAt().getTime()));
            prepSt1.setInt(4, tweet.getTwitterUser().getId());
            prepSt1.setString(5,"" + tweet.getAckState());
            prepSt1.executeUpdate();
            dbConnection.commit();

            System.out.println("Records are inserted into db!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (prepSt1 != null) {
                try {
                    prepSt1.close();
                } catch (SQLException e) {
                }
            }
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException e) {
                }
            }
        }

    }

    private Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            dbConnection = DriverManager.getConnection(
                    DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

}

