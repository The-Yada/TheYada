package com.theironyard.controllers;

import com.theironyard.entities.Link;
import com.theironyard.entities.User;
import com.theironyard.entities.Yada;
import com.theironyard.services.LinkRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.services.YadaRepository;
import com.theironyard.services.YadaUserJoinRepository;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by will on 7/18/16.
 */
@Controller
public class YadaController {

    @Autowired
    UserRepository users;

    @Autowired
    YadaRepository yadas;

    @Autowired
    YadaUserJoinRepository yadaUserJoinRepo;

    @Autowired
    LinkRepository links;


    @PostConstruct
    public void init() throws SQLException, FileNotFoundException {
//        if(users.count() == 0) {
//            parseUsers("users.csv");
//        }

        if (links.count() == 0) {
            parseLinks("links2.csv");
        }

        if (yadas.count() == 0) {
            parseYadas("yadas.csv");
        }


    }
    public void parseUsers(String fileName) throws FileNotFoundException {
        if (users.count() == 0) {
            File usersFile = new File(fileName);
            Scanner fileScanner = new Scanner(usersFile);
            fileScanner.nextLine();
            while (fileScanner.hasNext()) {
                String[] columns = fileScanner.nextLine().split(",");
                User user = new User(columns[0], Integer.valueOf(columns[1]));
                users.save(user);

            }
        }
    }
    //parsing links file
    public void parseLinks(String fileName) throws FileNotFoundException {
        if (links.count() == 0) {
            File linkFile = new File(fileName);
            Scanner linkScanner = new Scanner(linkFile);
            linkScanner.nextLine();
//            long i = 0;
//
//            Random r = new Random();
//            int maximum = 500;
//            int minimum = 0;
//            int range = maximum - minimum + 1;
//
//            int maxYada = 10;
//            int minYada = 1;
//            int rangeYada = maxYada - minYada + 1;


            while (linkScanner.hasNext()) {
//                int randomNumVotes = r.nextInt(range) + minimum;
//                int randomNumYadas = r.nextInt(rangeYada + minYada);
                String[] columns = linkScanner.nextLine().split(",");
                Link link = new Link(columns[0], LocalDateTime.now().minusSeconds(Long.valueOf(columns[2])), 0, Integer.valueOf(columns[1]), 5, 0);
                links.save(link);
//                i++;
//
            }
        }
    }
    //parsing yada file for dummy data
    public void parseYadas(String fileName) throws FileNotFoundException {
        if (yadas.count() == 0) {
            File yadaFile = new File(fileName);
            Scanner fileScanner = new Scanner(yadaFile);
            fileScanner.nextLine();
            while (fileScanner.hasNext()) {
                String[] columns = fileScanner.nextLine().split(",");
                Yada yada = new Yada(columns[0], Integer.valueOf(columns[1]), LocalDateTime.now(), Integer.valueOf(columns[2]), users.findOne(Integer.valueOf(columns[3])), links.findOne(Integer.valueOf(columns[4])));
                yadas.save(yada);

            }
        }
    }
}
