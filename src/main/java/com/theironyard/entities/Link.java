package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    int totalVotes;

    @Column(nullable = false)
    int numberOfYadas;

    @Column(nullable = false)
    long timeDiffInSeconds;

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL)
    List<Yada> yadaList;

    public Link() {
    }

    public Link(String url, LocalDateTime timeOfCreation, double linkScore, int totalVotes, int numberOfYadas, long timeDiffInSeconds) {
        this.url = url;
        this.timeOfCreation = timeOfCreation;
        this.linkScore = linkScore;
        this.totalVotes = totalVotes;
        this.numberOfYadas = numberOfYadas;
        this.timeDiffInSeconds = timeDiffInSeconds;
    }

    public Link(String url, LocalDateTime timeOfCreation, double linkScore, int totalVotes, int numberOfYadas, long timeDiffInSeconds, List<Yada> yadaList) {
        this.url = url;
        this.timeOfCreation = timeOfCreation;
        this.linkScore = linkScore;
        this.totalVotes = totalVotes;
        this.numberOfYadas = numberOfYadas;
        this.timeDiffInSeconds = timeDiffInSeconds;
        this.yadaList = yadaList;
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

    public int getNumberOfYadas() {
        return numberOfYadas;
    }

    public void setNumberOfYadas(int numberOfYadas) {
        this.numberOfYadas = numberOfYadas;
    }

    public long getTimeDiffInSeconds() {
        return timeDiffInSeconds;
    }

    public void setTimeDiffInSeconds(long timeDiffInSeconds) {
        this.timeDiffInSeconds = timeDiffInSeconds;
    }
}
