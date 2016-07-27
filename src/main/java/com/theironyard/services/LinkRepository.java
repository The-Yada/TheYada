package com.theironyard.services;

import com.theironyard.entities.Link;
import com.theironyard.entities.Yada;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;

/**
 * Created by jonathandavidblack on 7/20/16.
 */
public interface LinkRepository extends PagingAndSortingRepository<Link, Integer> {
    Link findFirstByUrl(String url);
    ArrayList<Link> findAllByOrderByLinkScoreDesc();
    ArrayList<Link> findTop5ByOrderByLinkScoreDesc();
    ArrayList<Link> findTop10ByOrderByLinkScoreDesc();
    ArrayList<Link> findTop25ByOrderByLinkScoreDesc();
    ArrayList<Link> findTop5ByOrderByTimeOfCreationAsc();
    ArrayList<Link> findTop10ByOrderByTimeOfCreationAsc();
    ArrayList<Link> findAllByOrderByControversyScoreDesc();
    @Query("SELECT l FROM Link l WHERE LOWER(title) LIKE '%' || LOWER(?) || '%'")
    Iterable<Link> searchByTitle(String searchInput);


}
