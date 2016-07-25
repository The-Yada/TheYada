package com.theironyard.controllers;

import com.theironyard.entities.Link;
import com.theironyard.entities.User;
import com.theironyard.entities.Yada;
import com.theironyard.entities.YadaUserJoin;
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
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
    public HttpStatus upVote(HttpSession session, int yadaUserJoinId, int userId, int yadaId){
        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        //*** we need to also account for users karma being altered when up and down votes are cast


        YadaUserJoin yadaUJoin = yadaUserJoinRepo.findOne(yadaUserJoinId);
        Yada yada = yadas.findOne(yadaId);

        if (yadaUJoin == null) {
            yadaUJoin.setUser(user);
            yadaUJoin.setYada(yada);
            yada.setKarma(yada.getKarma() + 1);
            yada.setUpvotes(yada.getUpvotes() + 1);
            yadaUserJoinRepo.save(yadaUJoin);
        }

        else {
           return HttpStatus.LOCKED;
        }

        return HttpStatus.ACCEPTED;
    }

    //hit this route so users can downVote yadas
    @RequestMapping(path = "/downVote", method = RequestMethod.POST)
    public HttpStatus downVote(int yadaUserJoinId, int userId, int yadaId){

        //*** we need to also account for users karma being altered when up and down votes are cast

        YadaUserJoin yadaUJoin = yadaUserJoinRepo.findOne(yadaUserJoinId);
        User user = users.findOne(userId);
        Yada yada = yadas.findOne(yadaId);

        if (yadaUJoin == null) {
            yadaUJoin.setUser(user);
            yadaUJoin.setYada(yada);
            yada.setKarma(yada.getKarma() - 1);
            yada.setDownvotes(yada.getDownvotes() + 1);
            yadaUserJoinRepo.save(yadaUJoin);
        }

        else {
            return HttpStatus.LOCKED;
        }

        return HttpStatus.ACCEPTED;
    }


    //route which brings user to the editing screen with scraped website text and submission box
    @RequestMapping(path = "/lemmieYada", method = RequestMethod.GET)
    public ArrayList<String> letMeYada(@RequestParam (value = "url", required = false) String url) throws IOException {

        //using jsoup method to grab website text
        ArrayList<String> scrapedSite = soupThatSite(url);

        return scrapedSite;
    }

    //hit this route to display yadas for a given webpage from the chrome extension
    @RequestMapping(path = "/lemmieSeeTheYadas", method = RequestMethod.GET)
    public Iterable<Yada> showMeTheYada(HttpSession session, @RequestParam (value = "url", required = false) String url) {

        String username = (String) session.getAttribute("username");
        if (username == null) {

        }

        Link link = links.findFirstByUrl(url);
        if (link == null) {

        }

        if(link.getYadaList() == null) {
            ArrayList<Yada> yadasInLink = new ArrayList<>();
            link.setYadaList(yadasInLink);
        }
        Iterable<Yada> theYadas = link.getYadaList();

        return theYadas;
    }

    @RequestMapping(path = "/addYada", method = RequestMethod.POST)
    public Iterable<Yada> addYada(HttpSession session, @RequestBody Yada yada) throws Exception {
        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);
        Yada firstYada = new Yada("Your Yada Here", 0, LocalDateTime.now(), 0, 0, 0, "", 0, yada.getUser(), links.findFirstByUrl(yada.getUrl()));

        if (username == null) {
            throw new Exception ("Not So Fast!!");
        }
        ArrayList<Yada> theYadasInside = new ArrayList<>();
        theYadasInside.add(yada);
        Link link = links.findFirstByUrl(yada.getUrl());

        Yada yada1 = new Yada(yada.getContent(), 0,  LocalDateTime.now(), 0, 0, 0, yada.getUrl(),  0, user, link);

        if(link.getYadaList() == null) {
            ArrayList<Yada> yadasInLink = new ArrayList<>();
            yadasInLink.add(firstYada);
            link.setYadaList(yadasInLink);
        }

        List<Yada> yadasInLink = link.getYadaList();
        yadasInLink.add(yada1);

        links.save(link);
        yadas.save(yada1);

        Iterable<Yada> updatedYadaList = link.getYadaList();

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
                parsedDoc.add(str);
            });

            doc.select("p").stream().filter(Element::hasText).forEach(element -> {
                String str = element.text();
                parsedDoc.add(str);
            });
        }

        return parsedDoc;
    }
}