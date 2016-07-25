package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by will on 7/18/16.
 */
@Entity
@Table(name="yadas")
public class Yada {
    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    String content;

    @Column(nullable = false)
    int karma;

    @Column(nullable = false)
    LocalDateTime time;

    @Column(nullable = false)
    int score;

    @Column(nullable = false)
    int upvotes;

    @Column(nullable = false)
    int downvotes;

    @Column(nullable = false)
    double controversyScore;

    @ManyToOne
    User user;

    @JsonIgnore
    @ManyToOne
    Link link;

    public Yada() {
    }

    public Yada(String content, int karma, LocalDateTime time, int score, int upvotes, int downvotes, double controversyScore, User user, Link link) {
        this.content = content;
        this.karma = karma;
        this.time = time;
        this.score = score;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.controversyScore = controversyScore;
        this.user = user;
        this.link = link;
    }

    public Yada(String content, int karma, LocalDateTime time, int score, Link link) {
        this.content = content;
        this.karma = karma;
        this.time = time;
        this.score = score;
        this.link = link;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public double getControversyScore() {
        return controversyScore;
    }

    public void setControversyScore(double controversyScore) {
        this.controversyScore = controversyScore;
    }


}
