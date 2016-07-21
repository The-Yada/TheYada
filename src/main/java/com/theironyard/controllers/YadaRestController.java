package com.theironyard.controllers;

import com.theironyard.entities.Link;
import com.theironyard.entities.Yada;
import com.theironyard.services.LinkRepository;
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
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.HashMap;

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
    public ArrayList<ArrayList<Yada>> getYadaList() {

        //sorted List of
        ArrayList<ArrayList<Yada>> sortedListOfYadaLists = new ArrayList<>();
        //get all urls
        ArrayList<Yada> allYadas = (ArrayList<Yada>) yadas.findAll();
        HashMap<Link, ArrayList<Yada>> yadaMap = new HashMap<>();
        for(Yada yada : allYadas) {
           // ArrayList<Yada> yadaListByUrl = yadaMap.get( );
        }
        return sortedListOfYadaLists;
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

    public ArrayList<Yada> sortYadasFromLink() {
        ArrayList<Yada> sortedYadas = new ArrayList<>();

        Link linkFromWhichTo



        return sortedYadas;
    }

}







