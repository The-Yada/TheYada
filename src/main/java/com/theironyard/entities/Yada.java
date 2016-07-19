package com.theironyard.entities;

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

    @Column
    String url;

    @Column(nullable = false)
    LocalDateTime time;

    @Column(nullable = false)
    int score;

    @ManyToOne
    User user;

    public Yada() {
    }

    public Yada(String content, int karma, String url, LocalDateTime time, int score, User user) {
        this.content = content;
        this.karma = karma;
        this.url = url;
        this.time = time;
        this.score = score;
        this.user = user;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public double getScore() {
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
}
