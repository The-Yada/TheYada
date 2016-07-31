package com.theironyard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theironyard.entities.Link;
import com.theironyard.entities.User;
import com.theironyard.entities.Yada;
import com.theironyard.entities.YadaLink;
import com.theironyard.services.LinkRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.services.YadaRepository;
import com.theironyard.services.YadaUserJoinRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TheYadaApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TheYadaApplicationTests {

	@Autowired
	UserRepository users;

	@Autowired
	YadaRepository yadas;

	@Autowired
	YadaUserJoinRepository yadaUserJoinRepo;

	@Autowired
	LinkRepository links;

	@Autowired
	WebApplicationContext wap;

	MockMvc mockMvc;

	@Before
	public void before() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wap).build();
	}

	@Test
	public void aTestAddYada() throws Exception {
		User user = new User("joe", "123", 0);
		users.save(user);
		Yada yada = new Yada();
		yada.setContent("content");
		Link link = new Link();
		link.setUrl("http://www.google.com");
		YadaLink yadaLink = new YadaLink(yada, link);


		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(yadaLink);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/addYada")
						.sessionAttr("username", "joe")
						.content(json)
						.contentType("application/json")
		);
		Assert.assertTrue(yadas.count() == 1);
	}

	@Test
	public void bTestUpVoteExtension() throws Exception {

		User user = new User("joey", "123");

		ArrayList<Yada> yadasInLink = new ArrayList<>();
		Link link = new Link("www.google.com", LocalDateTime.now(), 0, 1, 1, 0, 0, 0, 0,"alskdj", 1, yadasInLink);

		users.save(user);
		links.save(link);

		Yada yada = new Yada("content", 3, LocalDateTime.now(), 0, 0, 0, user, link);
		link.getYadaList().add(yada);
		links.save(link);
		yadas.save(yada);

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(yada);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/upVoteExt")
						.sessionAttr("username", "joey")
						.content(json)
						.contentType("application/json")
		);

		Yada yadaThatWasUpvoted = yadas.findOne(8);
		Assert.assertTrue(yadaThatWasUpvoted.getKarma() == 4);
	}

}


