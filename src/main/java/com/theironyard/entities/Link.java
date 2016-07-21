package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by jonathandavidblack on 7/20/16.
 */
@Entity
@Table(name = "links")
public class Link {

    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    public String url;

    @Column(nullable = false)
    LocalDateTime timeOfCreation;

    @Column(nullable = false)
    double linkScore;

    @Column(nullable = false)
    int yadaCount;

    @Column(nullable = false)
    int totalVotes;

    @OneToMany(mappedBy = "link")
    List<Yada> yadaList;

    public Link() {
    }

    public Link(String url, LocalDateTime timeOfCreation, double linkScore) {
        this.url = url;
        this.timeOfCreation = timeOfCreation;
        this.linkScore = linkScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(LocalDateTime timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public double getLinkScore() {
        return linkScore;
    }

    public void setLinkScore(double linkScore) {
        this.linkScore = linkScore;
    }

    public int getYadaCount() {
        return yadaCount;
    }

    public void setYadaCount(int yadaCount) {
        this.yadaCount = yadaCount;
    }

    public List<Yada> getYadaList() {
        return yadaList;
    }

    public void setYadaList(List<Yada> yadaList) {
        this.yadaList = yadaList;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }
}
