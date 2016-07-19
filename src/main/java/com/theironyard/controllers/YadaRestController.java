package com.theironyard.controllers;

import com.theironyard.entities.Yada;
import com.theironyard.services.UserRepository;
import com.theironyard.services.YadaRepository;
import com.theironyard.services.YadaUserJoinRepository;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by will on 7/18/16.
 */
@RestController
public class YadaRestController {

    @Autowired
    UserRepository users;

    @Autowired
    YadaRepository yadas;

    @Autowired
    YadaUserJoinRepository yadaUserJoinRepo;

    // start h2 database
//    @PostConstruct
//    public void init() throws SQLException, InterruptedException {
//        Server.createWebServer().start();
//
//        while (true) {
//            // sort posts, wait 1 second, infinitely
//            // sortPosts();
//            // Thread.sleep(1000);
//        }
//
//
//    }
//
//    public void sortYadasByScore() {
//        ArrayList<Yada> yadaList = (ArrayList<Yada>) yadas.findAll();
//        yadaList.parallelStream()
//
//    }



    // get route for yadas
//    @RequestMapping(path = "/yadas", method = RequestMethod.GET)
//    public Page<Yada> getYadas(Integer page, double weight) {
//
//        page = (page == null) ? 0 : page;
//        PageRequest pageRequest = new PageRequest(page, 10);
//
//        Page<Yada> pageOfYadas;
//        pageOfYadas = yadas.findByWeightDesc(pageRequest, weight);
//
//        return pageOfYadas;
//    }
}
