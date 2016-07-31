package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by jonathandavidblack on 7/18/16.
 */
@Entity
@Table(name = "yada_user_joins")
public class YadaUserJoin {

    @Id
    @GeneratedValue
    int id;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    User user;

    @JsonIgnore
    @ManyToOne
    Yada yada;

    @Column
    public Integer theYadaId;

    @Column(nullable = false)
    boolean upvoted = false;

    @Column(nullable = false)
    boolean downvoted = false;

    @Column(nullable = false)
    boolean author = false;


    public YadaUserJoin() {
    }

    public YadaUserJoin(User user, Yada yada, Integer theYadaId) {
        this.user = user;
        this.yada = yada;
        this.theYadaId = theYadaId;
    }

    public YadaUserJoin(User user, Yada yada, Integer theYadaId, boolean upvoted, boolean downvoted, boolean author) {
        this.user = user;
        this.yada = yada;
        this.theYadaId = theYadaId;
        this.upvoted = upvoted;
        this.downvoted = downvoted;
        this.author = author;
    }

    public YadaUserJoin(User user, Yada yada) {
        this.user = user;
        this.yada = yada;
    }

    public Integer getTheYadaId() {
        return theYadaId;
    }

    public void setTheYadaId(Integer theYadaId) {
        this.theYadaId = theYadaId;
    }

    public boolean isAuthor() {
        return author;
    }

    public void setAuthor(boolean author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Yada getYada() {
        return yada;
    }

    public void setYada(Yada yada) {
        this.yada = yada;
    }

    public boolean isUpvoted() {
        return upvoted;
    }

    public void setUpvoted(boolean upvoted) {
        this.upvoted = upvoted;
    }

    public boolean isDownvoted() {
        return downvoted;
    }

    public void setDownvoted(boolean downvoted) {
        this.downvoted = downvoted;
    }

}
