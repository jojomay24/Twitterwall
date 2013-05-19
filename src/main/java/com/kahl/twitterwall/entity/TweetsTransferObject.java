package com.kahl.twitterwall.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class TweetsTransferObject {

    public List<Tweet> tweets;
    public int totalFoundNr = 0;
    public int ackState = -1;
    public int offset = 0;


}
