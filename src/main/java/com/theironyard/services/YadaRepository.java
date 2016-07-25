package com.theironyard.services;

import com.theironyard.entities.Link;
import com.theironyard.entities.Yada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    List<Yada> findAllByLinkId(Link link);
    ArrayList<Yada> findAllByLinkIdOrderByControversyScoreDesc(Link link);
    //ArrayList<Yada> findTop10ByOrderByTimeOfCreationAsc();
    ArrayList<Yada> findAllByLinkIdOrderByKarmaDesc(Link link);


}
