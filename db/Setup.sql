CREATE TABLE TwitterUser (
  id                        INT NOT NULL AUTO_INCREMENT,

  name                      varchar(100) NOT NULL,
  profileImageUrl           VARCHAR(700),
  
  PRIMARY KEY(id),
  UNIQUE KEY idx_name_profileimg(name,profileImageUrl)
) engine=innodb

CREATE TABLE Tweet (
  id                        INT auto_increment,
  tweetId                   VARCHAR(25) NOT NULL COMMENT 'see Twitter API Tweet ID',
  text                      VARCHAR(250) DEFAULT "EMPTY",
  createdAt                 timestamp,
  twitterUserId             INT NOT NULL,
  ackState                  INT DEFAULT 0 COMMENT '0: not touched, 1: acked, 2: blocked',

  PRIMARY KEY (id),
  FOREIGN KEY (twitterUserId) REFERENCES TwitterUser(id)  ON DELETE CASCADE
) engine=innodb

