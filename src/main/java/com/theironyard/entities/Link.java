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

    @OneToMany(mappedBy = "link")
    List<Yada> yadaList;

    public Link() {
    }
}
