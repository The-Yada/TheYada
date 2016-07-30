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

    @Column
    String username;

    @Column
    String password;

    @Column
    int karma;

    @OneToMany(mappedBy = "user")
    public List<YadaUserJoin> yadaUserJoinList;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, int karma) {
        this.username = username;
        this.password = password;
        this.karma = karma;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }
    public List<YadaUserJoin> getYadaUserJoinList() {
        return yadaUserJoinList;
    }

    public void setYadaUserJoinList(List<YadaUserJoin> yadaUserJoinList) {
        this.yadaUserJoinList = yadaUserJoinList;
    }
}
