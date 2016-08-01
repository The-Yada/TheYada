package com.theironyard.services;

import com.theironyard.entities.Link;
import com.theironyard.entities.Yada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by will on 7/18/16.
 */
public interface YadaRepository extends CrudRepository<Yada, Integer> {
//   Page<Yada> findByScoreDesc(Pageable pageable, int score);
  // Iterable<Yada> findByScoreDesc(int score);
  //  List<Yada> findByUrl(String url);
//   Iterable<Yada> findByScoreAsc(int score);
    List<Yada> findAllByLinkId(int id);
    //ArrayList<Yada> findTop10ByOrderByTimeOfCreationAsc();
    Iterable<Yada> findTop10ByLinkIdOrderByKarmaDesc(int id);
    ArrayList<Yada> findAllByUserId(int id);

    // query below is for testing
    Yada findFirstByOrderByIdDesc();
    //Yada findFirstByOrderBy

    int countByLink(Link link);

    @Query("SELECT y FROM Yada y WHERE LOWER(content) LIKE '%' || LOWER(?) || '%'")
    Iterable<Yada> searchByContent(String searchInput);

}