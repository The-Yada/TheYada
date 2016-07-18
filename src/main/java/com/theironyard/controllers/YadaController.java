package com.theironyard.controllers;

import com.theironyard.entities.Yada;
import com.theironyard.services.UserRepository;
import com.theironyard.services.YadaRepository;
import com.theironyard.services.YadaUserJoinRepository;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Created by will on 7/18/16.
 */
@Controller
public class YadaController {

    @Autowired
    UserRepository users;

    @Autowired
    YadaRepository yadas;

    @Autowired
    YadaUserJoinRepository yadaUserJoinRepo;

    @PostConstruct
    public void init() throws SQLException, FileNotFoundException {
        Server.createWebServer().start();
        if (yadas.count() == 0) {
            parseYadas("yadas.csv");
        }
    }
    public void parseYadas(String fileName) throws FileNotFoundException {
        if (yadas.count() == 0) {
            File yadaFile = new File(fileName);
            Scanner fileScanner = new Scanner(yadaFile);
            fileScanner.nextLine();
            while (fileScanner.hasNext()) {
                String[] columns = fileScanner.nextLine().split(",");
                Yada yada = new Yada();
                yadas.save(yada);

            }

}
