package com.theironyard.controllers;

import com.theironyard.entities.Yada;
import com.theironyard.services.UserRepository;
import com.theironyard.services.YadaRepository;
import com.theironyard.services.YadaUserJoinRepository;
import org.h2.tools.Server;
import org.hibernate.jpa.event.internal.core.JpaSaveOrUpdateEventListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;

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
    @PostConstruct
    public void init() throws SQLException, IOException {
        Server.createWebServer().start();
        soupThatSite("http://www.npr.org/2016/07/18/486543063/trump-campaign-outlines-how-to-make-america-safe-again-on-first-night-of-rnc");

    }

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

    public String soupThatSite(String url) throws IOException {

        Document doc = Jsoup.connect(url).get();
        String text = "";

        for (Element element1 : doc.select("h1")) {

            if (element1.hasText()) {
                text.concat(element1.text());

            }
        }

        for( Element element : doc.select("p")) {

            if( element.hasText()) {
                text.concat((element.text()));
                System.out.println(element);
            }
        }

        return text;
    }
}
//    String fileToServe = "";
//doc.select("h1").stream().filter(Element::hasText).forEach(element -> fileToServe.concat(element.text()));
//        doc.select("p").stream().filter(Element::hasText).forEach(element -> fileToServe.concat(element.text()));
//        System.out.println(fileToServe);