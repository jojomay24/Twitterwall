package com.kahl.twitterwall.rest;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class GrizzlyServer implements Runnable{

    public static final URI BASE_URI = getBaseURI();

    private static Logger log = Logger.getLogger(GrizzlyServer.class);
    private static HttpServer httpServer;

    public GrizzlyServer() {
    }

    private static URI getBaseURI() {
//        return UriBuilder.fromUri("http://localhost/").port(9998).build();
        return UriBuilder.fromUri("http://192.168.1.52/").port(9998).build();
//        return UriBuilder.fromUri("http://217.110.29.210/").port(9998).build();
    }

    protected static HttpServer startServer() throws IOException {
        System.out.println("Starting grizzly...");
        ResourceConfig rc = new PackagesResourceConfig("com.sun.jersey.samples.helloworld.resources", "com.kahl.twitterwall.rest");
        rc.getContainerResponseFilters().add(new CORSFilter());
        rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        return GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
    }

    public static void stopGrizzlyServer() throws IOException {
        log.info("Stopping HTTP-Server");
        httpServer.stop();
    }

    @Override
    public void run() {
        try {
            httpServer = startServer();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nTry out %shelloworld\nHit enter to stop it...",
                BASE_URI, BASE_URI));
        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        httpServer.stop();
    }


}