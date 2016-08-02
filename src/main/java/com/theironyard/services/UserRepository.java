package com.theironyard.services;

import com.theironyard.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * Created by will on 7/18/16.
 */
public interface UserRepository extends CrudRepository<User, Integer>{
    User findFirstByUsername(String username);

    ArrayList<User> findTop10ByOrderByKarmaDesc();

    @Query("SELECT u FROM User u WHERE LOWER(username) LIKE '%' || LOWER(?) || '%'")
    Iterable<User> searchByAuthor(String searchInput);
}
