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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;


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

    @Autowired
    LinkRepository links;

    // start h2 database
    @PostConstruct
    public void init() throws SQLException, IOException {
        Server.createWebServer().start();
        soupThatSite("http://www.dw.com/de/frankreich-arbeitsmarktreform-light/a-19407655");
        ArrayList<Link> sortedLinkList = (ArrayList<Link>) links.findAll();
        sortLinkList(sortedLinkList);
    }

    //route which returns a sorted(by highest score) list of all yadaLists(based on url)
    @RequestMapping(path = "/theYadaList", method = RequestMethod.GET)
    public ArrayList<Link> getYadaList() {
        ArrayList<Link> allLinks = (ArrayList<Link>) links.findAll();
        ArrayList<Link> sortedListOfLinks = new ArrayList<>();


        ArrayList<ArrayList<Yada>> sortedListOfYadaLists = new ArrayList<>();
        //get all urls


        return sortedListOfLinks;
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

    public ArrayList<Link> sortLinkList(ArrayList<Link> list) {
        ArrayList<Link> sortedLinkList = new ArrayList<>();

        int yadaCountIterator = 1;
        int minimum = 0;
        int maximum = 700;

        for (Link link : list) {

            Random r = new Random();
            int yadaTotalVotesIterator = minimum + r.nextInt((maximum - minimum) + 1);
            link.setYadaCount(yadaCountIterator);
            link.setTotalVotes(yadaTotalVotesIterator);
            link.setLinkScore(link.getTotalVotes()/link.getYadaCount());
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
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(@RequestBody User user, HttpSession session) throws Exception {

        session.setAttribute("username", user.getUsername());
        return user;
    }
}







