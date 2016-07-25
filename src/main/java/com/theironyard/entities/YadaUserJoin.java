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

    @ManyToOne
    Yada yada;

    boolean hasUpvoted = false;

    boolean hasDownvoted = false;

    boolean hasVoted = false;

    public YadaUserJoin() {
    }

//    public YadaUserJoin(int id, User user, Yada yada) {
//        this.id = id;
//        this.user = user;
//        this.yada = yada;
//    }

    public YadaUserJoin(User user, Yada yada, boolean hasUpvoted, boolean hasDownvoted, boolean hasVoted) {
        this.user = user;
        this.yada = yada;
        this.hasUpvoted = hasUpvoted;
        this.hasDownvoted = hasDownvoted;
        this.hasVoted = hasVoted;
    }

    public YadaUserJoin(User user, Yada yada) {
        this.user = user;
        this.yada = yada;
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

    public boolean isHasUpvoted() {
        return hasUpvoted;
    }

    public void setHasUpvoted(boolean hasUpvoted) {
        this.hasUpvoted = hasUpvoted;
    }

    public boolean isHasDownvoted() {
        return hasDownvoted;
    }

    public void setHasDownvoted(boolean hasDownvoted) {
        this.hasDownvoted = hasDownvoted;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }
}
