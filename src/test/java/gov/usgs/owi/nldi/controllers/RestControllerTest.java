package gov.usgs.owi.nldi.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.usgs.owi.nldi.controllers.RestController;
import gov.usgs.owi.nldi.dao.StreamingDao;

public class RestControllerTest {

	private MockMvc mockMvc;
	
	@Mock
	private StreamingDao streamingDao;
	
	private RestController mvcService;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	mvcService = new RestController(streamingDao);
    	mockMvc = MockMvcBuilders.standaloneSetup(mvcService).build();
    }
    
	@Test
	public void getNavigationTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/navigation")).andExpect(status().isOk()).andReturn();

	}

}
