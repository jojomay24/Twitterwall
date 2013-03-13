import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class MysqlTest {

    public static String DB_CONNECTION = "jdbc:mysql://localhost/Twitterwall";
    public static String DB_USER = "Twitterwall";
    public static String DB_PASSWORD = "Twitterwall123";
    public static String DB_DRIVER = "com.mysql.jdbc.Driver";

    /**
     * @param args
     */
    public static void main(String[] args) {
        HashMap data = new HashMap();
        data.put("TWEET_ID", 1234556L);
        data.put("TEXT", "ALEX Test Text");
        data.put("ACK_STATE", 0);
        data.put("TWITTER_USER_IMAGE_URL","www.fcb.de");
        data.put("TWITTER_USER_NAME","Ollom2");

        try {
            // clearDatabase();
            writeToDb(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        readFromDb();
    }

    private static void clearDatabase() throws SQLException {
        System.out.println("Deleting");
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        String deleteTwitterUser = "DELETE FROM TwitterUser";
        // String deleteTweet = "DELETE FROM Tweet";
        try {
            dbConnection = getDBConnection();
            preparedStatement = dbConnection.prepareStatement(deleteTwitterUser);
            preparedStatement.executeUpdate();
            System.out.println("Records deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
        System.out.println("Deleted");
    }

    private static int getPrimaryKeyForTwitterUser(String name, String profileImageUrl) {
        Connection dbConnection = getDBConnection();
        PreparedStatement prepSt1 = null;
        int foundId = -1;

        String selectSQL = "SELECT id FROM TwitterUser WHERE name = ? and profileImageUrl = ?";
//        String selectSQL = "SELECT id FROM TwitterUser WHERE name = ?";
        try {
            prepSt1 = dbConnection.prepareStatement(selectSQL);
            prepSt1.setString(1, name);
            prepSt1.setString(2, profileImageUrl);
            ResultSet rs = prepSt1.executeQuery();
            while (rs.next()) {
                foundId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foundId;
    }

    private static int writeTwitterUserToDb(String name, String profileImageUrl) {
        Connection dbConnection = null;
        PreparedStatement prepSt1 = null;
        int genKey = -1;

        String insertTwitterUserSQL = "INSERT INTO Twitterwall.TwitterUser"
                + "(name, profileImageUrl) VALUES (?,?)";

        try {
            dbConnection = getDBConnection();

            prepSt1 = dbConnection.prepareStatement(insertTwitterUserSQL,PreparedStatement.RETURN_GENERATED_KEYS);
            prepSt1.setString(1, name);
            prepSt1.setString(2, profileImageUrl);
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

    private static void writeTweetToDb(Map data) {
        Connection dbConnection = null;
        PreparedStatement prepSt1 = null;

        String insertTweetSQL = "INSERT INTO Twitterwall.Tweet"
                + "(tweetId, text, createdAt, twitterUserId, ackState)"
                + "VALUES (?,?,?,?,?)";

        try {
            dbConnection = getDBConnection();
            dbConnection.setAutoCommit(false);

            prepSt1 = dbConnection.prepareStatement(insertTweetSQL);
            prepSt1.setLong(1, (Long)data.get("TWEET_ID"));
            prepSt1.setString(2, (String)data.get("TEXT"));
            prepSt1.setTimestamp(3, getCurrentTimeStamp());
            prepSt1.setInt(4,(Integer)data.get("TWITTER_USER_ID"));
            prepSt1.setString(5,"" + data.get("ACK_STATE"));
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

    private static void writeToDb(Map data) throws SQLException {
        System.out.println("Writing Entry to DB: ");
        int twitterUserKey = getPrimaryKeyForTwitterUser((String)data.get("TWITTER_USER_NAME"), (String)data.get("TWITTER_USER_IMAGE_URL"));
        if (-1 == twitterUserKey)
        {
            System.out.println("Couldn't find TwitterUser, creating ...");
            twitterUserKey = writeTwitterUserToDb((String)data.get("TWITTER_USER_NAME"), (String)data.get("TWITTER_USER_IMAGE_URL"));
        }

        data.put("TWITTER_USER_ID", twitterUserKey);
        System.out.println("Writing Tweet to DB");
        writeTweetToDb(data);
        System.out.println("Finished Writing");
      }

    private static Connection getDBConnection() {
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

    private static java.sql.Timestamp getCurrentTimeStamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

    }

    private static void readFromDb() {
        String dbUrl = "jdbc:mysql://localhost/Twitterwall";
        String dbClass = "com.mysql.jdbc.Driver";

        String username = "Twitterwall";
        String password = "Twitterwall123";

        String query = "Select * from Twitterwall.TwitterUser";

        try {
            Class.forName(dbClass);
            Connection connection = DriverManager.getConnection(dbUrl,
                    username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String result = resultSet.getString(3);
                System.out.println("Result : " + result);
            }
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
