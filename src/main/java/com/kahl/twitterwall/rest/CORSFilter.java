package com.kahl.twitterwall.rest;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Filter to handle cross-origin resource sharing.
 */
public class CORSFilter implements ContainerResponseFilter
{

    public CORSFilter()
    {
    }

    @Override
    public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response)
    {
        response.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHttpHeaders().add("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        response.getHttpHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        return response;
    }
}