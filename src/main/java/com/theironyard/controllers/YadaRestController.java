package com.theironyard.controllers;

import com.theironyard.entities.*;
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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by will on 7/18/16.
 */


@RestController
public class YadaRestController {

    /**
     * Instantiate variables for the HOT sorting algorithm
     */
    static final double GRAVITY = 1.8;
    static final int SECONDS_IN_TWO_HOURS = 7200;

    /**
     * Link up repositories to our controller.
     */
    @Autowired
    UserRepository users;

    @Autowired
    YadaRepository yadas;

    @Autowired
    YadaUserJoinRepository yadaUserJoinRepo;

    @Autowired
    LinkRepository links;

    /**
     * Start H2 database web server connection.
     * Considering deploying with PostgreSQL for the purpose of running stored procedures,
     * but this is unnecessary in the current state.
     */
    @PostConstruct
    public void init() throws SQLException, IOException {
        Server.createWebServer().start();
    }

    /**
     * Route for logging in. Doubles as a registration form.
     * Would like to refactor and have a separate route for registering an account.
     * Uses hashing to securely store the password.
     * More info on password hashing in Java:  https://github.com/defuse/password-hashing
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody User user, HttpSession session) throws Exception {
        User userFromDatabase = users.findFirstByUsername(user.getUsername());

        if (userFromDatabase == null) {
            user.setPassword(PasswordStorage.createHash(user.getPassword()));
            user.setUsername(user.getUsername());
            user.setKarma(0);
            users.save(user);
        }
        else if (!PasswordStorage.verifyPassword(user.getPassword(), userFromDatabase.getPassword())) {
            return new ResponseEntity<>("BAD PASS", HttpStatus.FORBIDDEN);
        }

        session.setAttribute("username", user.getUsername());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Logout route: invalidates session to log user out.
     */
    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(HttpSession session) {

        session.invalidate();

        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * Route for returning a list of all yada lists for a given URL.
     * Queries links table and returns links by their "linkscore."
     * Linkscore is a field in the link entity which is calculated through the generateLinkScore method.
     */
    @RequestMapping(path = "/theYadaList", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Link>> getYadaList() {

        ArrayList<Link> linkList = (ArrayList<Link>) links.findAll();
        generateLinkScore(linkList);

        return new ResponseEntity<>(links.findAllByOrderByLinkScoreDesc(), HttpStatus.OK);
    }

    /**
     * Route which finds links solely on overall Karma.
     * A link's karma can be found by subtracting downvotes from upvotes.
     */
    @RequestMapping(path = "/topLinks", method = RequestMethod.GET)
    public ArrayList<Link> getTopLinks() {

        return links.findTop10ByOrderByKarmaDesc();
    }

    /**
     * Route which finds the newest links based on their time of creation.
     */
    @RequestMapping(path = "/newLinks", method = RequestMethod.GET)
    public ArrayList<Link> getNewYadas() {

        return links.findTop10ByOrderByTimeOfCreationDesc();
    }

    /**
     * Route which returns a list of controversial links.
     * Links are determined to be controversial based on their controveryScore.
     * The controveryScore field is calculated by the generateControversyScore method.
     */
    @RequestMapping(path = "/controversialLinks", method = RequestMethod.GET)
    public ArrayList<Link> getControversialYadas() {

        ArrayList<Link> allLinks = (ArrayList<Link>) links.findAll();
        generateControveryScore(allLinks);

        return links.findAllByOrderByControversyScoreDesc();
    }

    /**
     * route to find yadas from a given user.
     */

    //hit this route to find top users yadas
    @RequestMapping(path = "/topUsersYadas", method = RequestMethod.GET)
    public LinkedHashMap<User, ArrayList<Yada>> getTopUsersYadas() {
        LinkedHashMap<User, ArrayList<Yada>> topUsersYadasMap = new LinkedHashMap<>();

        ArrayList<User> topUsers = users.findTop10OrderByKarmaDesc();

        for (User user : topUsers) {
            ArrayList<Yada> topUsersYadas = yadas.findAllByUserId(user.getId());
            topUsersYadasMap.put(user, topUsersYadas);
        }
        return topUsersYadasMap;
    }

    /**
     * Route which allows users to search for yadas from a search bar based on the content of the yada.
     * Returns any yada which has the exact string the user enters.
     * The content field is the user-generated blurb for a given article which is displayed and voted on.
     * If the user enters no text, currently all yadas are displayed. TODO: Address this.
     *
     * @param searchInput
     * @return
     */
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

    /**
     * Route which allows users to find links by the title of the article. TODO: implementation
     * The title of the article is generated when using the Jsoup library to scrape the text from a webpage.
     * More info/documentation on Jsoup: https://jsoup.org/
     *
     * @param searchInput
     * @return
     */
    @RequestMapping(path = "/searchTitles", method = RequestMethod.GET)
    public ResponseEntity getSearchResultsOfTitles(@RequestParam (value = "searchInput", required = false) String searchInput) {
        Iterable<Link> linksThatMatchSearchInput = new ArrayList<>();

        if (searchInput != null) {
            linksThatMatchSearchInput = links.searchByTitle(searchInput);

        }

        return new ResponseEntity<Iterable<Link>>(linksThatMatchSearchInput, HttpStatus.OK);
    }
    /**
     * This method returns the YadaUserJoin List for a given logged in User
     */

    @RequestMapping(path = "/yadaUserJoinList", method = RequestMethod.GET)
    public ResponseEntity getYadaUserJoinList(HttpSession session) {

        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        ArrayList<YadaUserJoin> yadaUserJoinsByUser = (ArrayList<YadaUserJoin>) user.getYadaUserJoinList();

        return new ResponseEntity(yadaUserJoinsByUser, HttpStatus.OK);
    }

    /**
     * Route which allows users to upvote yadas.
     * Operates by an established connection (join) between yadas and users in the YadaUserJoin table.
     * Upon creation of a yada through the /addYadas POST route, this connection is established for the author.
     *
     * Users may upvote, rescind their upvote, or change their vote to an upvote from a downvote.
     * This action alters the karma at all three levels: link, yada, and user (author of the yada).
     *
     * Requires being logged in.
     *
     * Might want to refactor this in order to clean this route up and stick the code in its own method.
     *
     * @param session
     * @param yada
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/upVote", method = RequestMethod.POST)
    public ResponseEntity upVote(HttpSession session, @RequestBody Yada yada) throws Exception {

        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        Yada yadaToUpVote = yadas.findOne(yada.getId());

        if (username != null) {

            if (yadaUserJoinRepo.findByUserAndYada(user, yada) == null) {
                YadaUserJoin yuj = new YadaUserJoin(user, yada, yada.getId());

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
                    
                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

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

                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

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

                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

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

                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

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

    /**
     * Route which allows users to downvote yadas.
     * Operates by an established connection (join) between yadas and users in the YadaUserJoin table.
     * Upon creation of a yada through the /addYadas POST route, this connection is established for the author.
     *
     * Users may downvote, rescind their downvote, or change their vote to a downvote from an upvote.
     * This action alters the karma at all three levels: link, yada, and user (author of the yada).
     *
     * Requires being logged in.
     *
     * Might want to refactor this in order to clean this route up and stick the code in its own method.
     *
     *
     * @param session
     * @param yada
     * @return
     */
    @RequestMapping(path = "/downVote", method = RequestMethod.POST)
    public ResponseEntity downVote(HttpSession session, @RequestBody Yada yada) {

        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        Yada yadaToDownVote = yadas.findOne(yada.getId());

        if (username != null) {

            if (yadaUserJoinRepo.findByUserAndYada(user, yada) == null) {
                YadaUserJoin yuj = new YadaUserJoin(user, yada, yada.getId());

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


                    yadas.save(yadaToDownVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

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


                    yadas.save(yadaToDownVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

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

                    yadas.save(yadaToDownVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);
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

                    yadas.save(yadaToDownVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);
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
    @RequestMapping(path = "/upVoteExt", method = RequestMethod.POST)
    public ResponseEntity upVoteExt(HttpSession session, @RequestBody Yada yada) throws Exception {

        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        Yada yadaToUpVote = yadas.findOne(yada.getId());

        if (username != null) {

            if (yadaUserJoinRepo.findByUserAndYada(user, yada) == null) {
                YadaUserJoin yuj = new YadaUserJoin(user, yada, yada.getId());

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


                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);
                    return new ResponseEntity<>(link, HttpStatus.OK);
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


                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);
                    return new ResponseEntity<>(link, HttpStatus.OK);
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



                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

                    return new ResponseEntity<>(link, HttpStatus.OK);
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


                    yadas.save(yadaToUpVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

                    return new ResponseEntity<>(link, HttpStatus.OK);
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

    /**
     * Route which allows users to downvote yadas.
     * Operates by an established connection (join) between yadas and users in the YadaUserJoin table.
     * Upon creation of a yada through the /addYadas POST route, this connection is established for the author.
     *
     * Users may downvote, rescind their downvote, or change their vote to a downvote from an upvote.
     * This action alters the karma at all three levels: link, yada, and user (author of the yada).
     *
     * Requires being logged in.
     *
     * Might want to refactor this in order to clean this route up and stick the code in its own method.
     *
     *
     * @param session
     * @param yada
     * @return
     */
    @RequestMapping(path = "/downVoteExt", method = RequestMethod.POST)
    public ResponseEntity downVoteExt(HttpSession session, @RequestBody Yada yada) {

        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        Yada yadaToDownVote = yadas.findOne(yada.getId());

        if (username != null) {

            if (yadaUserJoinRepo.findByUserAndYada(user, yada) == null) {
                YadaUserJoin yuj = new YadaUserJoin(user, yada, yada.getId());

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


                    yadas.save(yadaToDownVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

                    return new ResponseEntity<>(link, HttpStatus.OK);
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


                    yadas.save(yadaToDownVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

                    return new ResponseEntity<>(link, HttpStatus.OK);

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


                    yadas.save(yadaToDownVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

                    return new ResponseEntity<>(link, HttpStatus.OK);

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



                    yadas.save(yadaToDownVote);
                    yadaUserJoinRepo.save(yuj);
                    links.save(link);
                    users.save(yadaAuthor);

                    return new ResponseEntity<>(link, HttpStatus.OK);

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

    /**
     * This route invokes the soupThatSite method which scrapes a website for its title and main body text.
     * The scraped text is displayed inside the Chrome extension as a convenience to the yada author.
     * The soupThatSite method is made possible through Jsoup. More info on jsoup here: https://jsoup.org/
     *
     * @param url
     * @return
     * @throws IOException
     */

    @RequestMapping(path = "/lemmieYada", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<String>> letMeYada(@RequestParam (value = "url", required = false) String url) throws IOException {

        ArrayList<String> scrapedSite = soupThatSite(url);

        return new ResponseEntity<>(scrapedSite, HttpStatus.OK);
    }

    /**
     * Route which displays yadas for a given webpage while using the Chrome Extension.
     * Takes in url and finds the top yadas for a given link.
     *
     * Requires being logged in.
     *
     * @param session
     * @param url
     * @return
     */

    @RequestMapping(path = "/lemmieSeeTheYadas", method = RequestMethod.GET)
    public ResponseEntity showMeTheYada(HttpSession session, @RequestParam (value = "url", required = false) String url) {
        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);


        if (url.contains("?")) {
            String[] columns = url.split("\\?");
            String usableUrl = columns[0];

            Link link = links.findFirstByUrl(usableUrl);
            Iterable<Yada> yadasByKarma = yadas.findTop10ByLinkIdOrderByKarmaDesc(link.getId());

            if ((yadasByKarma == null)) {

                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(yadasByKarma, HttpStatus.OK);


        }

        else {

            Link link = links.findFirstByUrl(url);
            Iterable<Yada> yadasByKarma = yadas.findTop10ByLinkIdOrderByKarmaDesc(link.getId());

            if ((yadasByKarma == null)) {

                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(yadasByKarma, HttpStatus.OK);

        }
    }

    /**
     * Route to check if the user is logged in.
     * Required to persist log in from one tab to the next.
     *
     * @param session
     * @return
     */
    @RequestMapping(path = "/logStatus", method = RequestMethod.GET)
    public ResponseEntity checkIfUserIsLoggedIn(HttpSession session) {
        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        if (user == null) {
            return new ResponseEntity<>("not logged in", HttpStatus.BAD_REQUEST);
        }

        else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

    }

    /**
     * This route enables users to add yadas for an article through the chrome extension.
     * Populates the link table with a new record if there is no record yet, then creates a yada.
     * Inserts some starting/default values for each record, and adjusts the author's karma and sets his vote status for that yada.
     * Creates the YadaUserJoin connection.
     *
     * Uses a wrapper object called YadaLink to get two objects in the request body.
     *
     * Requires being logged in.
     *
     *
     * @param session
     * @param yl
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/addYada", method = RequestMethod.POST)
    public ResponseEntity addYada(HttpSession session, @RequestBody YadaLink yl) throws Exception {

        String username = (String) session.getAttribute("username");
        User user = users.findFirstByUsername(username);

        if (username == null) {

            return new ResponseEntity<>("Not So Fast", HttpStatus.FORBIDDEN);
        }

        String strippedUrl = yl.getLink().getUrl();
        Link link = links.findFirstByUrl(yl.getLink().getUrl());


        if (link == null) {
            if (strippedUrl.contains("?")) {
                String[] columns = strippedUrl.split("\\?");
                String usableUrl = columns[0];
                List<Yada> yadaListInLink = new ArrayList<>();

                link = new Link(usableUrl, LocalDateTime.now(), 0, 1, 1, 0, 0, 0, 0, soupThatSite(yl.getLink().getUrl()).get(0), 1, yadaListInLink);
                link = links.save(link);
            }

            else {
                List<Yada> yadaListInLink = new ArrayList<>();
                    link = new Link(yl.getLink().getUrl(), LocalDateTime.now(), 0, 1, 1, 0, 0, 0, 0, soupThatSite(yl.getLink().getUrl()).get(0), 1, yadaListInLink);


                link = links.save(link);
            }
        }

        if (link.getYadaList().size() >= 1) {
            List<Yada> yadasInLink = link.getYadaList();
            for (Yada y : yadasInLink) {
                if (user.getId() == y.getUser().getId()) {
                    return new ResponseEntity(HttpStatus.CONFLICT);
                }
            }
        }

        Yada yada = new Yada(yl.getYada().getContent(), 1, LocalDateTime.now(), 0, 1, 0, user, link);
        yadas.save(yada);
        YadaUserJoin yuj = new YadaUserJoin(user, yada, yada.getId(), true, false, true);

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


    /**
     * This method defines the algorithm which generates linkscore for each record in the link table.
     * This value is calculated and updated only upon hitting the front page's GET route.
     * This might be moved to operate as a stored procedure if we switch to running PostgreSQL instead of H2.
     *
     * The sorting algoritm equation is as follows:
     *  Link score = (link karma - number of yadas) / ((time elapsed since creation + constant of 2 hrs) raised to a constant of 1.8 (Gravity)).
     *
     * The design of this algorithm was inspired from reddit's and Hacker News's sorting algorithms.
     *
     * More info on reddit's sorting algorithms: https://medium.com/hacking-and-gonzo/how-reddit-ranking-algorithms-work-ef111e33d0d9#.ndo001s22
     * More info on Hacker News's sorting algorithm: https://medium.com/hacking-and-gonzo/how-hacker-news-ranking-algorithm-works-1d9b0cf2c08d#.i0p8oh1pk
     *
     *
     * @param linkList
     * @return
     */
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

    /**
     * This method generates the controversyScore field for each link.
     *
     * This method runs when users hit the controversy score sorting option and calculates the score and then sorts links.
     *
     * The algorithm is as follows:
     * Controversy Score = (link's total votes) / (higher value of absolute value of (upvotes - downvotes) compared to 1)
     *
     * Inspired by reddit's controversial sorting algorithm.
     *
     * More info on reddit's controversial algorithm: https://medium.com/hacking-and-gonzo/how-reddit-ranking-algorithms-work-ef111e33d0d9#.m7sca64af
     *
     *
     * @param linkList
     * @return
     */
    public List<Link> generateControveryScore(ArrayList<Link> linkList) {

        for (Link link : linkList) {

            link.setControversyScore((link.getTotalVotes()) / Math.max((Math.abs(link.getUpVotes() - link.getDownVotes())), 1));
            links.save(link);

        }
        return linkList;
    }

    /**
     * This method uses the Jsoup library which scrapes the web for text.
     * It seems as if most websites are safe to scrape by looking for their H1 element and their paragraph elements.
     * e.g.: CNN's website is slightly different, and we have accounted for it below.
     *
     * Jsoup also prevents against XSS (cross site scripting), and we have employed that functionality in this method.
     *
     * Might have to account for other websites in the future if scraping is not done properly.
     * Could implement a way for users to notify us of a site which is not scraping properly.
     *
     * More info on Jsoup here: https://jsoup.org
     *
     * @param url
     * @return
     * @throws IOException
     */
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