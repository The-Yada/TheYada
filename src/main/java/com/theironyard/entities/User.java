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
    String nickname;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String email;


    @Column
    int karma;

    @OneToMany(mappedBy = "user")
    public List<YadaUserJoin> yadaUserJoinList;

    public User() {
    }

    public User(String nickname, String name, String email, int karma) {
        this.nickname = nickname;
        this.name = name;
        this.email = email;
        this.karma = karma;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
