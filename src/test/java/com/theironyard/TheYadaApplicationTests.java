package com.theironyard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theironyard.entities.Link;
import com.theironyard.entities.User;
import com.theironyard.entities.Yada;
import com.theironyard.services.LinkRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.services.YadaRepository;
import com.theironyard.services.YadaUserJoinRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class TheYadaApplicationTests {

	@Autowired
	WebApplicationContext wap;

	@Autowired
	UserRepository users;

	@Autowired
	YadaRepository yadas;

	@Autowired
	YadaUserJoinRepository yadaUserJoinRepo;

	@Autowired
	LinkRepository links;

	MockMvc mockMvc;

	@Before
	public void before() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wap).build();
	}

	@Test
	public void addYada() throws Exception {
		User user = new User();
		ArrayList<Yada> yadasInLink = new ArrayList<>();
 		Link link = new Link("www.google.com", LocalDateTime.now(), 0, 1, 1, 0, 0, 0, 0,"asldkfja", 1, yadasInLink);

		users.save(user);
		links.save(link);

		Yada yada = new Yada();
		yada.setContent("Test Yada");
		yada.setDownvotes(0);
		yada.setKarma(1);
		yada.setUpvotes(1);
		yada.setUser(user);
		yada.setTime(LocalDateTime.now());
		yada.setLink(link);
		yadas.save(yada);


		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(yada);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/addYada")
						.content(json)
						.contentType("application/json")
		);

		Assert.assertTrue(yadas.count() == 1);
	}

	@Test
	public void testLemmeSeeTheYadas() throws Exception {
		User user = new User("testName", "testPass");


		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/lemmieSeeTheYadas")
						.content(json)
						.contentType("application/json")
		);

		Assert.assertTrue(link.getYadaList().size() > 0);
		Assert.assertTrue(!testUrl.contains("?"));

	}

}


