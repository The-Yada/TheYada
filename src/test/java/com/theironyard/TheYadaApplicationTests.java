package com.theironyard;

import com.fasterxml.jackson.core.type.TypeReference;
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
	public void bTestAddYada() throws Exception {
		User user = new User("joe", "123", 0);
		users.save(user);
		Yada yada = new Yada();
		yada.setContent("content");
		Link link = new Link();
		link.setKarma(5);
		link.setUrl("http://www.bbc.com/sport/formula1/36879742");
		YadaLink yadaLink = new YadaLink(yada, link);


		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
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
	public void cTestAddYada2() throws Exception {
		User user = new User("joe2", "123", 0);
		users.save(user);
		Yada yada = new Yada();
		yada.setContent("content2");
		Link link = new Link();
		link.setUrl("http://www.bbc.com/news/business-36791928");
		YadaLink yadaLink = new YadaLink(yada, link);


		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		String json = mapper.writeValueAsString(yadaLink);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/addYada")
						.sessionAttr("username", "joe2")
						.content(json)
						.contentType("application/json")
		);


		Assert.assertTrue(yadas.count() == 2);
	}

//	@Test
//	public void cTestUpVoteExtension() throws Exception {
//
//		User user = new User("joey", "123");
//
//		ArrayList<Yada> yadasInLink = new ArrayList<>();
//		Link link = new Link("www.google.com", LocalDateTime.now(), 0, 1, 1, 0, 0, 0, 0,"alskdj", 1, yadasInLink);
//
//		users.save(user);
//		links.save(link);
//
//		Yada yada = yadas.findOne(0);
//		link.getYadaList().add(yada);
//		links.save(link);
//
//		ObjectMapper mapper = new ObjectMapper();
//		String json = mapper.writeValueAsString(yada);
//
//		mockMvc.perform(
//				MockMvcRequestBuilders.post("/upVoteExt")
//						.sessionAttr("username", "joey")
//						.content(json)
//						.contentType("application/json")
//		);
//
//		Yada yadaThatWasUpvoted = yadas.findOne(0);
//		Assert.assertTrue(yadaThatWasUpvoted.getKarma() == 4);
//	}

	@Test
	public void dTestLemmeSeeTheYadas() throws Exception {

		ResultActions ra = mockMvc.perform(
				MockMvcRequestBuilders.get("/lemmieSeeTheYadas")
						.param("url", "http://www.bbc.com/sport/formula1/36879742")
		);

		MvcResult result = ra.andReturn();
		MockHttpServletResponse response = result.getResponse();
		String json = response.getContentAsString();

		ObjectMapper om = new ObjectMapper();
		om.findAndRegisterModules();

		Iterable<Yada> testYadaList = om.readValue(json, new TypeReference<Iterable<Yada>>(){});


		ArrayList<Yada> testList = (ArrayList<Yada>) testYadaList;

		Assert.assertTrue(testList.size() == 1);
		Assert.assertTrue(testList.get(0).getContent().equals("content"));

	}

	@Test
	public void eTestTheYadaList() throws Exception {

		ResultActions ra = mockMvc.perform(
				MockMvcRequestBuilders.get("/theYadaList")
		);

		MvcResult result = ra.andReturn();
		MockHttpServletResponse response = result.getResponse();
		String json = response.getContentAsString();

		ObjectMapper om = new ObjectMapper();
		om.findAndRegisterModules();

		ArrayList<Link> testList = om.readValue(json, new TypeReference<ArrayList<Link>>(){});

		Assert.assertTrue(testList.size() == 2);
		Assert.assertTrue(testList.get(0).getUrl().equals("http://www.bbc.com/sport/formula1/36879742"));
	}

	@Test
	public void fTestLemmieYada() throws Exception {
		ResultActions ra = mockMvc.perform(
				MockMvcRequestBuilders.get("/lemmieYada")
						.param("url", "http://www.bbc.com/sport/formula1/36879742")
		);

		MvcResult result = ra.andReturn();
		MockHttpServletResponse response = result.getResponse();
		String json = response.getContentAsString();

		ObjectMapper om = new ObjectMapper();
		om.findAndRegisterModules();

		ArrayList<String> scrapedSite = om.readValue(json, new TypeReference<ArrayList<String>>(){});

		Assert.assertTrue(scrapedSite.get(0).contentEquals("Lewis Hamilton and Nico Rosberg clash off the track at Hungarian GP"));
	}

	@Test
	public void gTestTopLinks() throws Exception {
		ResultActions ra = mockMvc.perform(
				MockMvcRequestBuilders.get("/topLinks")
		);

		MvcResult result = ra.andReturn();
		MockHttpServletResponse response = result.getResponse();
		String json = response.getContentAsString();

		ObjectMapper om = new ObjectMapper();
		om.findAndRegisterModules();

		ArrayList<Link> testTopResults = om.readValue(json, new TypeReference<ArrayList<Link>>(){});

		Assert.assertTrue(testTopResults.size() == 2);
		Assert.assertTrue(testTopResults.get(0).getKarma() >= testTopResults.get(1).getKarma());


		// want to add a test to make sure that links with higher score are first in the array
	}

	@Test
	public void hTestNewLinks() throws Exception {
		ResultActions ra = mockMvc.perform(
				MockMvcRequestBuilders.get("/newLinks")
		);

		MvcResult result = ra.andReturn();
		MockHttpServletResponse response = result.getResponse();
		String json = response.getContentAsString();

		ObjectMapper om = new ObjectMapper();
		om.findAndRegisterModules();

		ArrayList<Link> testNewResults = om.readValue(json, new TypeReference<ArrayList<Link>>(){});

		Assert.assertTrue(testNewResults.size() == 2);
		Assert.assertTrue(testNewResults.get(0).getTimeOfCreation().isAfter(testNewResults.get(1).getTimeOfCreation()));
	}

//	@Test
//	public void xxTestUpVoteExtension() throws Exception {
//
//		User user = new User("joey", "123");
//
//		Link link = links.findFirstByUrl("")
//
//
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.findAndRegisterModules();
//		String json = mapper.writeValueAsString(yada);
//
//		mockMvc.perform(
//				MockMvcRequestBuilders.post("/upVoteExt")
//						.sessionAttr("username", "joey")
//						.content(json)
//						.contentType("application/json")
//		);
//
//		Yada yadaThatWasUpvoted = yadas.findOne(0);
//		Assert.assertTrue(yadaThatWasUpvoted.getKarma() == 4);
//	}



}


