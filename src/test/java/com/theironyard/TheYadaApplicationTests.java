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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
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
	public void aTestLogin() throws Exception {
		User user = new User("Tom", "password");

		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(user);


		mockMvc.perform(
				MockMvcRequestBuilders.post("/login")
						.content(json)
						.contentType("application/json")

		);
		Assert.assertTrue(user.getUsername().equals("Tom") && users.count() == 1);
	}

	@Test
	public void cTestUpvote() throws Exception {

	}






//	@Test
//	public void addYada() throws Exception {
//		User user = new User();
//		ArrayList<Yada> yadasInLink = new ArrayList<>();
// 		Link link = new Link("www.google.com", LocalDateTime.now(), 0, 1, 1, 0, 0, 0, 0,"asldkfja", 1, yadasInLink);
//
//		users.save(user);
//		links.save(link);
//
//		Yada yada = new Yada();
//		yada.setContent("Test Yada");
//		yada.setDownvotes(0);
//		yada.setKarma(1);
//		yada.setUpvotes(1);
//		yada.setUser(user);
//		yada.setTime(LocalDateTime.now());
//		yada.setLink(link);
//		yadas.save(yada);
//
//
//		ObjectMapper mapper = new ObjectMapper();
//		String json = mapper.writeValueAsString(yada);
//
//		mockMvc.perform(
//				MockMvcRequestBuilders.post("/addYada")
//						.content(json)
//						.contentType("application/json")
//		);
//
//		Assert.assertTrue(yadas.count() == 1);
//	}


//	@Test
//	public void testLemmeSeeTheYadas() throws Exception {
//
//		ResultActions ra = mockMvc.perform(
//				MockMvcRequestBuilders.get("/lemmieSeeTheYadas")
//		);
//
//		MvcResult result = ra.andReturn();
//		MockHttpServletResponse resp = result.getResponse();
//		String json = resp.getContentAsString();
//
//		ObjectMapper mapper = new ObjectMapper();
//		Yada yada = mapper.readValue(json, Yada.class);
//
//
//
//		Assert.assertTrue(
//	}

}


