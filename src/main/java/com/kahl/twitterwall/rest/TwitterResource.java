package com.kahl.twitterwall.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahl.twitterwall.service.TwitterService;
     // The Java class will be hosted at the URI path "/twitter"
     @Path("/twitter")
     @Service
     public class TwitterResource {

         @Autowired
         private TwitterService twitterService;

         // The Java method will process HTTP GET requests
         @GET
         // The Java method will produce content identified by the MIME Media
         // type "text/plain"
         @Produces("text/plain")
         public String getClichedMessage() {
             // Return some cliched textual content
             return twitterService.getTweetFromDb();
         }
     }