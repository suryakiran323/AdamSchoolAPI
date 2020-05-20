package com.springboottest.springboottestapp;


import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.stu.app.controller.BaseController;

@RunWith(SpringJUnit4ClassRunner.class)
public class HelloWorldTest {
	MockMvc mockMvc;

	@InjectMocks
	private BaseController helloWorldRes;
	
	@Before
	public void setup() throws Exception{
		mockMvc = MockMvcBuilders.standaloneSetup(helloWorldRes).build();
	}
	
	@Test
	public void testHellWorld() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/api/status"))
		.andExpect(MockMvcResultMatchers.status().isOk());
		//.andExpect(jsonPath("$.message", hasItem("OK")));
	}

}
