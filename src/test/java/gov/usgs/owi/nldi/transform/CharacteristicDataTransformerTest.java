package gov.usgs.owi.nldi.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gov.usgs.owi.nldi.dao.BaseDao;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

public class CharacteristicDataTransformerTest {

  private static final String INIT_JSON = "{\"comid\":\"comm\",\"characteristics\":[";
  private static final String WRITE_MAP =
      "{\"characteristic_id\":\"charID\",\"characteristic_value\":\"val\",\"percent_nodata\":\"none\"}";
  protected MockHttpServletResponse response;
  protected CharacteristicDataTransformer transformer;

  @BeforeEach
  public void beforeTest() throws IOException {
    response = new MockHttpServletResponse();
    transformer = new CharacteristicDataTransformer(response);
  }

  @AfterEach
  public void afterTest() throws Exception {
    transformer.close();
  }

  @Test
  public void initJsonTest() {
    transformer.initJson(transformer.jsonGenerator, buildMap());
    try {
      transformer.jsonGenerator.flush();
      assertEquals(INIT_JSON, response.getContentAsString());
    } catch (IOException e) {
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void initJsonTestBad() {
    boolean runtimeExceptionThrown = false;
    try {
      transformer.initJson(transformer.jsonGenerator, null);
      transformer.jsonGenerator.flush();
      assertEquals(INIT_JSON, response.getContentAsString());

    } catch (RuntimeException e) {
      runtimeExceptionThrown = true;
    } catch (Throwable t) {
      fail(t);
    }
    assertTrue(runtimeExceptionThrown);
  }

  @Test
  public void writeMapTest() {
    transformer.writeMap(transformer.jsonGenerator, buildMap());
    try {
      transformer.jsonGenerator.flush();
      assertEquals(WRITE_MAP, response.getContentAsString());
    } catch (IOException e) {
      fail(e.getLocalizedMessage());
    }
  }

  public Map<String, Object> buildMap() {
    Map<String, Object> map = new HashMap<>();
    map.put(BaseDao.COMID, "comm");
    map.put(CharacteristicMetadataTransformer.CHARACTERISTIC_ID, "charID");
    map.put(CharacteristicDataTransformer.CHARACTERISTIC_VALUE, "val");
    map.put(CharacteristicDataTransformer.PERCENT_NO_DATA, "none");
    return map;
  }
}
