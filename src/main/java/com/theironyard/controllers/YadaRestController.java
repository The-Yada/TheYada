package com.theironyard.controllers;

import com.theironyard.entities.Link;
import com.theironyard.entities.User;
import com.theironyard.entities.Yada;
import com.theironyard.services.LinkRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.services.YadaRepository;
import com.theironyard.services.YadaUserJoinRepository;
import org.h2.tools.Server;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

import java.util.List;
import java.util.HashMap;

/**
 * Created by will on 7/18/16.
 */

@RestController
public class YadaRestController {

    static final double GRAVITY = 1.8;
    static final int SECONDS_IN_TWO_HOURS = 7200;

    @Autowired
    UserRepository users;

    @Autowired
    YadaRepository yadas;

    @Autowired
    YadaUserJoinRepository yadaUserJoinRepo;

    @Autowired
    LinkRepository links;

    // start h2 database
    @PostConstruct
    public void init() throws SQLException, IOException {
        Server.createWebServer().start();
        soupThatSite("http://www.dw.com/de/frankreich-arbeitsmarktreform-light/a-19407655");

    }

    // route which returns a sorted(by highest score) list of all yadaLists(based on url)
    @RequestMapping(path = "/theYadaList", method = RequestMethod.GET)
    public ArrayList<Link> getYadaList() {

        ArrayList<Link> linkList = (ArrayList<Link>) links.findAll();
        generateLinkScore(linkList);
        return links.findAllByOrderByLinkScoreDesc();
    }

    //this method takes in a url, scrapes the associated site, and returns the scraped content as an arrayList of String
    public ArrayList<String> soupThatSite(String url) throws IOException {
        ArrayList<String> parsedDoc = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();

        if (url.contains("cnn.com")) {

            doc.select("h1").stream().filter(Element::hasText).forEach(element1 -> {
                String str = element1.text();
                parsedDoc.add(str);
            });

            doc.select(".zn-body__paragraph").stream().filter(Element::hasText).forEach(element1 -> {
                String str = element1.text();
                parsedDoc.add(str);
            });
        } else {

            doc.select("h1").stream().filter(Element::hasText).forEach(element1 -> {
                String str = element1.text();
                parsedDoc.add(str);
            });

            doc.select("p").stream().filter(Element::hasText).forEach(element -> {
                String str = element.text();
                parsedDoc.add(str);
            });
        }
        //System.out.println(parsedDoc);

        return parsedDoc;
    }

    // algo attempt 1
    public List<Link> generateLinkScore(ArrayList<Link> linkList) {

        for (Link link : linkList) {
            long difference = ChronoUnit.SECONDS.between(link.getTimeOfCreation(), LocalDateTime.now());
            link.setTimeDiffInSeconds(difference);
            long denominator = (difference + SECONDS_IN_TWO_HOURS);
            link.setLinkScore(((link.getTotalVotes() - link.getNumberOfYadas()) / (Math.pow(denominator, GRAVITY))));
            links.save(link);
        }
        return linkList;
    }

    @RequestMapping(path = "/addYada", method = RequestMethod.POST)
    public void addYada(String content, String url, String username) {
        Link link = links.findFirstByUrl(url);
        User user = users.findFirstByUsername(username);
        Yada yada = new Yada(content, 0, LocalDateTime.now(), 0, user, link);
        ArrayList<Yada> yadasInLink = (ArrayList<Yada>) link.getYadaList();
        yadasInLink.add(yada);
        yadas.save(yada);
        links.save(link);
    }


    public ArrayList<Link> sortLinkList(ArrayList<Link> list) {
        ArrayList<Link> sortedLinkList = new ArrayList<>();

        int yadaCountIterator = 1;
        int minimum = 0;
        int maximum = 700;

        for (Link link : list) {

            Random r = new Random();
            int yadaTotalVotesIterator = minimum + r.nextInt((maximum - minimum) + 1);
            link.setNumberOfYadas(yadaCountIterator);
            link.setTotalVotes(yadaTotalVotesIterator);
            link.setLinkScore(link.getTotalVotes() / link.getNumberOfYadas());
            yadaCountIterator++;
            links.save(link);
            sortedLinkList.add(link);
        }
        return sortedLinkList;
    }

    public ArrayList<Yada> sortYadasFromLink(Integer id) {
        ArrayList<Yada> sortedYadas = new ArrayList<>();

        Link linkFromWhichTo = links.findOne(id);


        return sortedYadas;
    }
}