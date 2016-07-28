package com.theironyard.controllers;

import com.theironyard.entities.*;
import com.theironyard.services.LinkRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.services.YadaRepository;
import com.theironyard.services.YadaUserJoinRepository;
import com.theironyard.utils.PasswordStorage;
import org.h2.tools.Server;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
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
    public ResponseEntity<User> login(@RequestBody User user, HttpSession session) throws Exception {
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
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(HttpSession session) {

        session.invalidate();

        return new ResponseEntity<>("Please Come Again Soon", HttpStatus.OK);

    }

    // route which returns a sorted(by highest score) list of all yadaLists(based on url)
    @RequestMapping(path = "/theYadaList", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Link>> getYadaList() {

        ArrayList<Link> linkList = (ArrayList<Link>) links.findAll();
        generateLinkScore(linkList);

        return new ResponseEntity<>(links.findAllByOrderByLinkScoreDesc(), HttpStatus.OK);
    }
    @RequestMapping(path = "/topLinks", method = RequestMethod.GET)
    public ArrayList<Link> getTopLinks() {

        return links.findTop10ByOrderByKarmaDesc();
    }
    @RequestMapping(path = "/newLinks", method = RequestMethod.GET)
    public ArrayList<Link> getNewYadas() {

        return links.findTop10ByOrderByTimeOfCreationDesc();
    }

    // find controversial yadas inside chrome extension for the article you're on
    @RequestMapping(path = "/controversialLinks", method = RequestMethod.GET)
    public ArrayList<Link> getControversialYadas() {

        ArrayList<Link> allLinks = (ArrayList<Link>) links.findAll();
        generateControveryScore(allLinks);

        return links.findAllByOrderByControversyScoreDesc();
    }

    //hit this route when searching through content of yadas
    @RequestMapping(path = "/searchYadas", method = RequestMethod.GET)
    public ResponseEntity getSearchResults(@RequestParam (value = "searchInput", required = false) String searchInput) {

        Iterable<Yada> yadasThatMatchSearchInput;
        ArrayList<Link> linksThatMatchSearchResults = new ArrayList<>();

        if (searchInput != null) {
            yadasThatMatchSearchInput = yadas.searchByContent(searchInput);
        }
        else {
            return new ResponseEntity<>("Please Type in Valid Search Term", HttpStatus.EXPECTATION_FAILED);
        }

        for (Yada yada : yadasThatMatchSearchInput) {
            Link linkToAddToSearchResults = yada.getLink();

            if (!linksThatMatchSearchResults.contains(linkToAddToSearchResults)) {
                linksThatMatchSearchResults.add(linkToAddToSearchResults);
            }
        }
        return new ResponseEntity<Iterable<Link>>(linksThatMatchSearchResults, HttpStatus.OK);
    }
    //hit this route to search by title
    @RequestMapping(path = "/searchTitles", method = RequestMethod.GET)
    public ResponseEntity getSearchResultsOfTitles(@RequestParam (value = "searchInput", required = false) String searchInput) {
        Iterable<Link> linksThatMatchSearchInput = new ArrayList<>();

        if (searchInput != null) {
            linksThatMatchSearchInput = links.searchByTitle(searchInput);

        }

        return new ResponseEntity<Iterable<Link>>(linksThatMatchSearchInput, HttpStatus.OK);
    }


    //hit this route so users can upVote yadas
    @RequestMapping(path = "/upVote", method = RequestMethod.POST)
    public ResponseEntity upVote(HttpSession session, @RequestBody Yada yada) throws Exception {

        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        Yada yadaToUpVote = yadas.findOne(yada.getId());

        if (username != null) {

            if (yadaUserJoinRepo.findByUserAndYada(user, yada) == null) {
                YadaUserJoin yuj = new YadaUserJoin(user, yada);

                if (!yuj.isUpvoted() && !yuj.isDownvoted()) {

                    yadaToUpVote.setKarma(yuj.getYada().getKarma() + 1);
                    yadaToUpVote.setUpvotes(yuj.getYada().getUpvotes() + 1);
                    User yadaAuthor = yuj.getYada().getUser();
                    yadaAuthor.setKarma(yadaAuthor.getKarma() + 1);
                    yuj.setUpvoted(true);
                    yuj.setDownvoted(false);

                    //link calculations
                    Link link = links.findOne(yadaToUpVote.getLink().getId());
                    link.setTotalVotes(link.getTotalVotes() + 1);
                    link.setUpVotes(link.getUpVotes() + 1);

                    users.save(yadaAuthor);
                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);

                    return new ResponseEntity<>(links.findAllByOrderByLinkScoreDesc(), HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
            else {
                YadaUserJoin yuj = yadaUserJoinRepo.findByUserAndYada(user, yada);

                if (!yuj.isUpvoted() && yuj.isDownvoted()) {

                    yadaToUpVote.setKarma(yuj.getYada().getKarma() + 2);
                    yadaToUpVote.setUpvotes(yuj.getYada().getUpvotes() + 1);
                    yadaToUpVote.setDownvotes(yuj.getYada().getDownvotes() - 1);
                    User yadaAuthor = yuj.getYada().getUser();
                    yadaAuthor.setKarma(yadaAuthor.getKarma() + 2);
                    yuj.setUpvoted(true);
                    yuj.setDownvoted(false);

                    //link calculations
                    Link link = links.findOne(yadaToUpVote.getLink().getId());
                    link.setUpVotes(link.getUpVotes() + 1);
                    link.setDownVotes(link.getDownVotes() - 1);

                    users.save(yadaAuthor);
                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);

                    return new ResponseEntity<>(links.findAllByOrderByLinkScoreDesc(), HttpStatus.OK);
                }
                else if (!yuj.isUpvoted() && !yuj.isDownvoted()) {

                    yadaToUpVote.setKarma(yuj.getYada().getKarma() + 1);
                    yadaToUpVote.setUpvotes(yuj.getYada().getUpvotes() + 1);
                    User yadaAuthor = yuj.getYada().getUser();
                    yadaAuthor.setKarma(yadaAuthor.getKarma() + 1);
                    yuj.setUpvoted(true);
                    yuj.setDownvoted(false);

                    //link calculations
                    Link link = links.findOne(yadaToUpVote.getLink().getId());
                    link.setUpVotes(link.getUpVotes() + 1);
                    link.setTotalVotes(link.getTotalVotes() + 1);


                    users.save(yadaAuthor);
                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);

                    return new ResponseEntity<>(links.findAllByOrderByLinkScoreDesc(), HttpStatus.OK);
                }
                else if (yuj.isUpvoted() && !yuj.isDownvoted()) {

                    yadaToUpVote.setKarma(yuj.getYada().getKarma() - 1);
                    yadaToUpVote.setUpvotes(yuj.getYada().getUpvotes() - 1);
                    User yadaAuthor = yuj.getYada().getUser();
                    yadaAuthor.setKarma(yadaAuthor.getKarma() - 1);
                    yuj.setUpvoted(false);

                    //link calculations
                    Link link = links.findOne(yadaToUpVote.getLink().getId());
                    link.setUpVotes(link.getUpVotes() - 1);
                    link.setTotalVotes(link.getTotalVotes() - 1);

                    users.save(yadaAuthor);
                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);

                    return new ResponseEntity<>(links.findAllByOrderByLinkScoreDesc(), HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    //hit this route so users can downvote yadas
    @RequestMapping(path = "/downVote", method = RequestMethod.POST)
    public ResponseEntity downVote(HttpSession session, @RequestBody Yada yada) {

            String username = (String) session.getAttribute("username");
            User user = users.findFirstByUsername(username);

            Yada yadaToDownVote = yadas.findOne(yada.getId());

            if (username != null) {

                if (yadaUserJoinRepo.findByUserAndYada(user, yada) == null) {
                    YadaUserJoin yuj = new YadaUserJoin(user, yada);

                    if (!yuj.isUpvoted() && !yuj.isDownvoted()) {

                        yadaToDownVote.setKarma(yuj.getYada().getKarma() - 1);
                        yadaToDownVote.setDownvotes(yuj.getYada().getDownvotes() + 1);
                        User yadaAuthor = yuj.getYada().getUser();
                        yadaAuthor.setKarma(yadaAuthor.getKarma() - 1);
                        yuj.setDownvoted(true);

                        //link calculations
                        Link link = links.findOne(yadaToDownVote.getLink().getId());
                        link.setDownVotes(link.getDownVotes() + 1);
                        link.setTotalVotes(link.getTotalVotes() + 1);

                        users.save(yadaAuthor);
                        yadas.save(yadaToDownVote);
                        yadaUserJoinRepo.save(yuj);
                        links.save(link);

                        return new ResponseEntity<>(links.findAllByOrderByLinkScoreDesc(), HttpStatus.OK);
                    }
                    else {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }
                else {

                    YadaUserJoin yuj = yadaUserJoinRepo.findByUserAndYada(user, yada);

                    if (yuj.isUpvoted() && !yuj.isDownvoted()) {

                        yadaToDownVote.setKarma(yuj.getYada().getKarma() - 2);
                        yadaToDownVote.setUpvotes(yuj.getYada().getUpvotes() - 1);
                        yadaToDownVote.setDownvotes(yuj.getYada().getDownvotes() + 1);
                        User yadaAuthor = yuj.getYada().getUser();
                        yadaAuthor.setKarma(yadaAuthor.getKarma() - 2);
                        yuj.setUpvoted(false);
                        yuj.setDownvoted(true);

                        //link calculations
                        Link link = links.findOne(yadaToDownVote.getLink().getId());
                        link.setDownVotes(link.getDownVotes() + 1);
                        link.setUpVotes(link.getUpVotes() - 1);

                        users.save(yadaAuthor);
                        yadas.save(yadaToDownVote);
                        yadaUserJoinRepo.save(yuj);
                        links.save(link);

                        return new ResponseEntity<>(links.findAllByOrderByLinkScoreDesc(), HttpStatus.OK);

                    }
                    else if (!yuj.isUpvoted() && !yuj.isDownvoted()) {

                        yadaToDownVote.setKarma(yuj.getYada().getKarma() - 1);
                        yadaToDownVote.setDownvotes(yuj.getYada().getDownvotes() + 1);
                        User yadaAuthor = yuj.getYada().getUser();
                        yadaAuthor.setKarma(yadaAuthor.getKarma() - 1);
                        yuj.setDownvoted(true);

                        //link calculations
                        Link link = links.findOne(yadaToDownVote.getLink().getId());
                        link.setDownVotes(link.getDownVotes() + 1);
                        link.setTotalVotes(link.getTotalVotes() + 1);


                        users.save(yadaAuthor);
                        yadas.save(yadaToDownVote);
                        yadaUserJoinRepo.save(yuj);
                        links.save(link);

                        return new ResponseEntity<>(links.findAllByOrderByLinkScoreDesc(), HttpStatus.OK);

                    }
                    else if (!yuj.isUpvoted() && yuj.isDownvoted()){
                        yadaToDownVote.setKarma(yuj.getYada().getKarma() + 1);
                        yadaToDownVote.setDownvotes(yuj.getYada().getDownvotes() - 1);
                        User yadaAuthor = yuj.getYada().getUser();
                        yadaAuthor.setKarma(yadaAuthor.getKarma() + 1);
                        yuj.setDownvoted(false);

                        //link calculations
                        Link link = links.findOne(yadaToDownVote.getLink().getId());
                        link.setDownVotes(link.getDownVotes() - 1);
                        link.setTotalVotes(link.getTotalVotes() - 1);


                        users.save(yadaAuthor);
                        yadas.save(yadaToDownVote);
                        yadaUserJoinRepo.save(yuj);
                        links.save(link);

                        return new ResponseEntity<>(links.findAllByOrderByLinkScoreDesc(), HttpStatus.OK);

                    }
                    else {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);

            }

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
    public ResponseEntity showMeTheYada(HttpSession session, @RequestParam (value = "url", required = false) String url) {

        Link link = links.findFirstByUrl(url);
        if (link == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Iterable<Yada> yadasByKarma = yadas.findTop10ByLinkIdOrderByKarmaDesc(link.getId());

        if ((yadasByKarma == null)) {

            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }

        return new ResponseEntity<>(yadasByKarma, HttpStatus.OK);
    }

    @RequestMapping(path = "/addYada", method = RequestMethod.POST)
    public ResponseEntity addYada(HttpSession session, @RequestBody YadaLink yl) throws Exception {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return new ResponseEntity<>("Not So Fast", HttpStatus.FORBIDDEN);
        }

        Link link = links.findFirstByUrl(yl.getLink().getUrl());

        if (link == null) {
            link = new Link(yl.getLink().getUrl(), LocalDateTime.now(), 0, 1, 1, 0, 0, 0, 0, soupThatSite(yl.getLink().getUrl()).get(0));
            link = links.save(link);
        }

        User user = users.findFirstByUsername(username);
        Yada yada = new Yada(yl.getYada().getContent(), 1, LocalDateTime.now(), 0, 1, 0, user, link);
        yadas.save(yada);
        YadaUserJoin yuj = new YadaUserJoin(user, yada, true, false);

        user.setKarma(user.getKarma() + 1);
        users.save(user);

        if (link.getYadaList() == null) {
            ArrayList<Yada> newYadaListInLink = new ArrayList<>();
            link.setYadaList(newYadaListInLink);
        }

        link.getYadaList().add(yada);
        link.setNumberOfYadas(link.getNumberOfYadas() + 1);
            links.save(link);
            yadaUserJoinRepo.save(yuj);

        Iterable<Yada> updatedYadaList = link.getYadaList();

        return new ResponseEntity<>(updatedYadaList, HttpStatus.OK);
    }

    // sorting algorithm - HOT (time/votes)
    public List<Link> generateLinkScore(ArrayList<Link> linkList) {

        for (Link link : linkList) {
            long difference = ChronoUnit.SECONDS.between(link.getTimeOfCreation(), LocalDateTime.now());
            link.setTimeDiffInSeconds(difference);
            long denominator = (difference + SECONDS_IN_TWO_HOURS);
            // numerator needs to account for personal vote?
            //
            link.setKarma(link.getUpVotes() - link.getDownVotes());
            link.setLinkScore(((link.getKarma() - link.getYadaList().size()) / (Math.pow(denominator, GRAVITY))));
            links.save(link);
        }
        return linkList;
    }


    // sorting algorithm - CONTROVERSIAL
    public List<Link> generateControveryScore(ArrayList<Link> linkList) {

        for (Link link : linkList) {

            link.setControversyScore((link.getTotalVotes()) / Math.max((Math.abs(link.getUpVotes() - link.getDownVotes())), 1));
            links.save(link);

        }
        return linkList;
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