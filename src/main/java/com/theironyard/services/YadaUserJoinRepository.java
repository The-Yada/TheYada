package com.theironyard.services;

import com.theironyard.entities.User;
import com.theironyard.entities.Yada;
import com.theironyard.entities.YadaUserJoin;
import org.springframework.data.repository.CrudRepository;

import java.util.HashMap;

/**
 * Created by jonathandavidblack on 7/18/16.
 */
public interface YadaUserJoinRepository extends CrudRepository<YadaUserJoin, Integer> {
     YadaUserJoin findByUserAndYada(User user, Yada yada);
}
