package gov.usgs.owi.nldi.services;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.owi.nldi.BaseIT;
import gov.usgs.owi.nldi.DBIntegrationTest;
import gov.usgs.owi.nldi.dao.LogDao;
import gov.usgs.owi.nldi.dao.LogDaoTest;

@Category(DBIntegrationTest.class)
@DatabaseSetup("classpath:/testData/webServiceLog.xml")
public class LogServiceTest extends BaseIT {

	@Autowired
	private LogService logService;

	@Test
	@ExpectedDatabase(
			value="classpath:/testResult/log/start.xml",
			table="nldi_data.web_service_log",
			query=LogDaoTest.TEST_QUERY,
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={IdModifier.class, DateTimeModifier.class})
	public void logRequestTest() {
		MockHttpServletRequest request= new MockHttpServletRequest();
		request.addHeader(LogDao.REFERER, LogDaoTest.REFERER_TEST_VALUE);
		request.addHeader(LogDao.USER_AGENT, LogDaoTest.USER_AGENT_TEST_VALUE);
		request.setRequestURI(LogDaoTest.REQUEST_URI_TEST_VALUE);
		request.setQueryString(LogDaoTest.QUERY_STRING_TEST_VALUE);
		id = logService.logRequest(request);
	}

	@Test
	@ExpectedDatabase(
			value="classpath:/testResult/log/end.xml",
					table="nldi_data.web_service_log",
					query=LogDaoTest.TEST_QUERY,
					assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
					modifiers=DateTimeModifier.class)
	public void logRequestCompleteTest() {
		logService.logRequestComplete(LogDaoTest.ID_TEST_VALUE, LogDaoTest.HTTP_STATUS_CODE_TEST_VALUE);
	}

}
