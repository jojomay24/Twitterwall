Twitterwall
===========
A simple Twitterwall solution with smartphone management capabilities by jojomay3@gmx.de

Components:
-----------
 * java-based Server
   * performs hashtag based twitter searches using the Twitter API
   * saves found tweets into the database
   * offers a REST interface to read / accept / reject tweets (for management cpabilities)
   * Technologies: Java, hibernate 4, Spring 3, twitter4j, Quartz scheduler, MySQL, Grizzly Http Server
 * javascript-based smartphone app 
   * empowers accepting or rejecting tweets before they are displayed on a twitterwall
   * uses the server component REST API
   * Technologies: javascript, jquery mobile, html5
 * twitterwall:
   * displays the tweets
   * soon to come :)
 


 
