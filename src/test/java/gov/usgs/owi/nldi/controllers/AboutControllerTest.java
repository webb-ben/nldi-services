package gov.usgs.owi.nldi.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import gov.usgs.owi.nldi.BaseSpringTest;
import gov.usgs.owi.nldi.FullIntegrationTest;

@Category(FullIntegrationTest.class)
public class AboutControllerTest extends BaseSpringTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void getAboutTest() throws Exception {
		mockMvc.perform(get("/about"))
				.andExpect(status().isOk())
				.andExpect(view().name("about"))
				.andExpect(model().attributeExists("version", "userGuide"))
				.andReturn();
	}

	@Test
	public void getSpecifiedPageTest() throws Exception {
		mockMvc.perform(get("/about/test"))
				.andExpect(status().isOk())
				.andExpect(view().name("test"))
				.andExpect(model().attributeExists("version", "userGuide"))
				.andReturn();
	}

}
