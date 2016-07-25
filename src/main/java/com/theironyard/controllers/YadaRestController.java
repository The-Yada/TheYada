package com.theironyard.controllers;

import com.theironyard.entities.User;
import com.theironyard.entities.Yada;
import com.theironyard.entities.Link;
import com.theironyard.entities.YadaLink;
import com.theironyard.services.LinkRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.services.YadaRepository;
import com.theironyard.services.YadaUserJoinRepository;
import com.theironyard.utils.PasswordStorage;
import org.h2.tools.Server;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(@RequestBody User user, HttpSession session) throws Exception {
        User userFromDatabase = users.findFirstByUsername(user.getUsername());

        if (userFromDatabase == null) {
            user.setPassword(PasswordStorage.createHash(user.getPassword()));
            user.setUsername(user.getUsername());
            users.save(user);
        }
        else if (!PasswordStorage.verifyPassword(user.getPassword(), userFromDatabase.getPassword())) {
            throw new Exception("BAD PASS");
        }

        session.setAttribute("username", user.getUsername());
        return user;
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
        return "";

    }

    // route which returns a sorted(by highest score) list of all yadaLists(based on url)
    @RequestMapping(path = "/theYadaList", method = RequestMethod.GET)
    public ArrayList<Link> getYadaList() {

        ArrayList<Link> linkList = (ArrayList<Link>) links.findAll();
        generateLinkScore(linkList);
        return links.findAllByOrderByLinkScoreDesc();
    }

    @RequestMapping(path = "/newYadas", method = RequestMethod.GET)
    public ArrayList<Link> getNewYadas() {

        return links.findTop10ByOrderByTimeOfCreationAsc();
    }

    // find controversial yadas inside chrome extension for the article you're on
    @RequestMapping(path = "/controversialYadas", method = RequestMethod.GET)
    public ArrayList<Yada> getControversialYadas(String url) {

        Link link = links.findFirstByUrl(url);
        ArrayList<Yada> yadaList = (ArrayList<Yada>) yadas.findAllByLinkId(link);
        generateControveryScore(yadaList);

        return yadas.findAllByLinkIdOrderByControversyScoreDesc(link);
    }

    //hit this route so users can upVote yadas
    @RequestMapping(path = "/upVote", method = RequestMethod.POST)
    public ResponseEntity upVote(HttpSession session, @RequestBody Yada yada){

        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        Yada yadaToUpVote = yadas.findOne(yada.getId());

            yadaToUpVote.setKarma(yada.getKarma() + 1);
            yadaToUpVote.setUpvotes(yada.getUpvotes() + 1);
            User yadaAuthor = yada.getUser();
            yadaAuthor.setKarma(yadaAuthor.getKarma() + 1);
            yadas.save(yadaToUpVote);

        return new ResponseEntity(HttpStatus.OK);
    }


    //hit this route so users can downVote yadas
    @RequestMapping(path = "/downVote", method = RequestMethod.POST)
    public ResponseEntity downVote(HttpSession session, @RequestBody Yada yada) {

        //*** we need to also account for users karma being altered when up and down votes are cast
        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        Yada yadaToUpVote = yadas.findOne(yada.getId());

        yadaToUpVote.setKarma(yada.getKarma() - 1);
        yadaToUpVote.setDownvotes(yada.getDownvotes() + 1);
        User yadaAuthor = yada.getUser();
        yadaAuthor.setKarma(yadaAuthor.getKarma() - 1);
        yadas.save(yadaToUpVote);

        return new ResponseEntity(HttpStatus.OK);
    }


    //route which brings user to the editing screen with scraped website text and submission box
    @RequestMapping(path = "/lemmieYada", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<String>> letMeYada(@RequestParam (value = "url", required = false) String url) throws IOException {

        //using jsoup method to grab website text
        ArrayList<String> scrapedSite = soupThatSite(url);

        return new ResponseEntity<>(scrapedSite, HttpStatus.OK);
    }

    //hit this route to display yadas for a given webpage from the chrome extension
    @RequestMapping(path = "/lemmieSeeTheYadas", method = RequestMethod.GET)
    public ResponseEntity<Iterable<Yada>> showMeTheYada(HttpSession session, @RequestParam (value = "url", required = false) String url) {

        String username = (String) session.getAttribute("username");
        if (username == null) {

        }
        Link link = links.findFirstByUrl(url);
        Iterable<Yada> yadasByKarma = yadas.findTop10ByLinkIdOrderByKarmaDesc(link.getId());

        return new ResponseEntity<>(yadasByKarma, HttpStatus.OK);
    }

    @RequestMapping(path = "/addYada", method = RequestMethod.POST)
    public Iterable<Yada> addYada(HttpSession session, @RequestBody YadaLink yl) throws Exception {


        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception ("Not So Fast!!");
        }

        Link link = links.findFirstByUrl(yl.getLink().getUrl());
        if (link == null) {
            link = new Link(yl.getLink().getUrl(), LocalDateTime.now(), 0, 0, 1, 0);
        }

        User user = users.findFirstByUsername(username);
        Yada yada = new Yada(yl.getYada().getContent(), 0, LocalDateTime.now(), 0, 0, 0, 0, user, link);
        if (yl.getLink().getYadaList() != null) {
            ArrayList<Yada> yadasInLink = (ArrayList<Yada>) yl.getLink().getYadaList();
            yadasInLink.add(yada);
            links.save(link);
            yadas.save(yada);

        }
        else {
            ArrayList<Yada> yadasInLink = new ArrayList<>();
            yadasInLink.add(yada);
            yl.getLink().setYadaList(yadasInLink);
            links.save(link);
            yadas.save(yada);

        }


        Iterable<Yada> updatedYadaList = yl.getLink().getYadaList();

        return updatedYadaList;
    }

    // sorting algorithm - HOT (time/votes)
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

    // sorting algorithm - CONTROVERSIAL
    public List<Yada> generateControveryScore(ArrayList<Yada> yadaList) {

        for (Yada yada : yadaList) {
            int upvotes = yada.getUpvotes();
            int downvotes = yada.getDownvotes();
            int totalVotes = yada.getUpvotes() + Math.abs(yada.getDownvotes());
            yada.setKarma(upvotes - downvotes); //*
            yada.setControversyScore((totalVotes) / Math.max(Math.abs(upvotes - downvotes), 1));
            yadas.save(yada);
        }
        return yadaList;
    }

    //this method takes in a url, scrapes the associated site, and returns the scraped content as an arrayList of String
    public ArrayList<String> soupThatSite(String url) throws IOException {
        ArrayList<String> parsedDoc = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();

        if (url.contains("cnn.com")) {

            doc.select("h1").stream().filter(Element::hasText).forEach(element1 -> {
                String str = element1.text();
                String clean = Jsoup.clean(str, Whitelist.basic());
                parsedDoc.add(clean);
            });

            doc.select(".zn-body__paragraph").stream().filter(Element::hasText).forEach(element1 -> {
                String str = element1.text();
                String clean = Jsoup.clean(str, Whitelist.basic());
                parsedDoc.add(clean);
            });

        } else {

            doc.select("h1").stream().filter(Element::hasText).forEach(element1 -> {
                String str = element1.text();
                String clean = Jsoup.clean(str, Whitelist.basic());
                parsedDoc.add(clean);
            });

            doc.select("p").stream().filter(Element::hasText).forEach(element -> {
                String str = element.text();
                String clean = Jsoup.clean(str, Whitelist.basic());
                parsedDoc.add(clean);
            });
        }

        return parsedDoc;
    }
}