package com.theironyard.services;

import com.theironyard.entities.Link;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * Created by jonathandavidblack on 7/20/16.
 */
public interface LinkRepository extends CrudRepository<Link, Integer> {
    Link findFirstByUrl(String url);
    ArrayList<Link> findTop10ByOrderByLinkScoreDesc(ArrayList<Link> links);
}
