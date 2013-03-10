CREATE TABLE TwitterUser (
  id                        int auto_increment,
  name                      varchar(100),
  profileImageUrl           VARCHAR(2083)
  primary key(id)
) engine=innodb

CREATE TABLE Tweet (
  id                        INT auto_increment,
  tweetId                   long,
  imgUrl                    VARCHAR(2083),
  text                      VARCHAR(250),   //egtl. 140

  createdAt                 timestamp,
  twitterUserId             VARCHAR(32),
  
  
  FOREIGN KEY (twitterUserId)
      REFERENCES TwitterUser(id)
  primary key(id)
) engine=innodb


