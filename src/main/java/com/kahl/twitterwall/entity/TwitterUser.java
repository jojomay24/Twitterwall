package com.kahl.twitterwall.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "TwitterUser",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "profileImageUrl" }) })
public class TwitterUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String profileImageUrl;

    @OneToMany(mappedBy="twitterUser")
    private Set<Tweet> tweets;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TwitterUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Override
    public String toString() {
        return "" + name + " : " + profileImageUrl;
    }

}
