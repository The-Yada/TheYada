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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
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
        soupThatSite("http://www.dw.com/de/frankreich-arbeitsmarktreform-light/a-19407655");
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
        return links.findTop5ByOrderByLinkScoreDesc();
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

        return yadas.findAllByLinkIdOrderByControversyScoreAsc(link);
    }






    //route which brings user to the editing screen with scraped website text and submission box
    @RequestMapping(path = "/lemmieYada", method = RequestMethod.GET)
    public ArrayList<String> letMeYada(@RequestParam (value = "url", required = false) String url) throws IOException {

        ArrayList<String> scrapedSite = soupThatSite(url);

        return scrapedSite;
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
            yada.setKarma(upvotes - downvotes);
            yada.setControversyScore((totalVotes) / Math.max(Math.abs(upvotes - downvotes), 1));
            yadas.save(yada);
        }
        return yadaList;
    }

    //hit this route to upvote or downvote yadas
    //not sure how to grab userId from Oauth
    @RequestMapping(path = "/upOrDownVote", method = RequestMethod.POST)
    public void Vote(int id, int voteInt) {
        //need user info to check if they've already voted

        Yada yada = yadas.findOne(id);
        if (voteInt == 1) {
            yada.setKarma(yada.getKarma() + 1);
            yada.setUpvotes(yada.getUpvotes() + 1);
        }
        if(voteInt == -1) {
            yada.setKarma(yada.getKarma() - 1);
            yada.setDownvotes(yada.getDownvotes() + 1);
        }
        yadas.save(yada);

    }
    //hit this route so users can upVote yadas
    @RequestMapping(path = "/upVote", method = RequestMethod.POST)
    public HttpStatus upVote(int yadaUserJoinId, int userId, int yadaId){

        YadaUserJoin yadaUJoin = yadaUserJoinRepo.findOne(yadaUserJoinId);
        User user = users.findOne(userId);
        Yada yada = yadas.findOne(yadaId);

        if (yadaUJoin == null) {
            yadaUJoin.setUser(user);
            yadaUJoin.setYada(yada);
            yada.setKarma(yada.getKarma() + 1);
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

        YadaUserJoin yadaUJoin = yadaUserJoinRepo.findOne(yadaUserJoinId);
        User user = users.findOne(userId);
        Yada yada = yadas.findOne(yadaId);

        if (yadaUJoin == null) {
            yadaUJoin.setUser(user);
            yadaUJoin.setYada(yada);
            yada.setKarma(yada.getKarma() - 1);
            yadaUserJoinRepo.save(yadaUJoin);
        }

        else {
            return HttpStatus.LOCKED;
        }

        return HttpStatus.ACCEPTED;
    }

    //hit this route to display yadas for a given webpage from the chrome extension
    @RequestMapping(path = "/lemmieSeeTheYadas", method = RequestMethod.GET)
    public Iterable<Yada> showMeTheYada(@RequestParam (value = "url", required = false) String url) {

        Link link = links.findFirstByUrl(url);
        Iterable<Yada> theYadas = link.getYadaList();

        return theYadas;
    }

    @RequestMapping(path = "/addYada", method = RequestMethod.POST)
    public HttpStatus addYada(String content, String url, String username) {
        Link link = links.findFirstByUrl(url);
        User user = users.findFirstByUsername(username);
        //Yada yada = new Yada(content, 0, LocalDateTime.now(), 0, user, link);
        Yada yada = new Yada(content, 0, LocalDateTime.now(), 0, 0, 0, 0, user, link);
        ArrayList<Yada> yadasInLink = (ArrayList<Yada>) link.getYadaList();
        yadasInLink.add(yada);
        yadas.save(yada);
        links.save(link);
        return HttpStatus.ACCEPTED;
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
}