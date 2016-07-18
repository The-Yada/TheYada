package com.theironyard.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by jonathandavidblack on 7/18/16.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    String username;

    @Column
    int karma;

    @OneToMany(mappedBy = "user")
    public List<YadaUserJoin> yadaUserJoinList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public User(String username, int karma) {

        this.username = username;
        this.karma = karma;
    }

    public List<YadaUserJoin> getYadaUserJoinList() {
        return yadaUserJoinList;
    }

    public void setYadaUserJoinList(List<YadaUserJoin> yadaUserJoinList) {
        this.yadaUserJoinList = yadaUserJoinList;
    }

    public User(int id, String username, int karma) {

        this.id = id;
        this.username = username;
        this.karma = karma;
    }
}
