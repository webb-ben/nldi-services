package gov.usgs.owi.nldi.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

public class MapToJsonTransformerTest {

  private static final String INITIAL_JSON = "{\"baz\":[";

  protected TestTransformer testTransformer;
  protected MockHttpServletResponse response;

  private class TestTransformer extends MapToJsonTransformer {
    public int initJsonCalledCount = 0;
    public int writeMapCalledCount = 0;

    public TestTransformer(HttpServletResponse response) throws IOException {
      super(response);
    }

    void assertMethodCallCounts(int initJsonCount, int writeMapCount) {
      assertEquals(initJsonCount, initJsonCalledCount);
      assertEquals(writeMapCount, writeMapCalledCount);
    }

    @Override
    void initJson(JsonGenerator jsonGenerator, Map<String, Object> resultMap) {
      initJsonCalledCount = initJsonCalledCount + 1;
      try {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("baz");
        jsonGenerator.writeStartArray();
      } catch (IOException e) {
        fail(e.getLocalizedMessage());
      }
    }

    @Override
    void writeMap(JsonGenerator jsonGenerator, Map<String, Object> resultMap) {
      writeMapCalledCount = writeMapCalledCount + 1;
      try {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(
            "prop" + writeMapCalledCount, "propValue" + writeMapCalledCount);
        jsonGenerator.writeEndObject();
      } catch (IOException e) {
        fail(e.getLocalizedMessage());
      }
    }
  }

  @BeforeEach
  public void beforeTest() throws IOException {
    response = new MockHttpServletResponse();
    testTransformer = new TestTransformer(response);
  }

  @AfterEach
  public void afterTest() throws Exception {
    testTransformer.close();
  }

  @Test
  public void writeTest() {
    // Don't process null results
    testTransformer.write((Object) null);
    testTransformer.assertMethodCallCounts(0, 0);

    // Don't process results that aren't a map
    testTransformer.write((Object) "ABCDEFG");
    testTransformer.assertMethodCallCounts(0, 0);

    Map<String, Object> result = new HashMap<>();
    result.put("A", "1");
    result.put("B", "2");

    testTransformer.write((Object) result);
    testTransformer.assertMethodCallCounts(1, 1);

    // initial json should be set after first call, along with first property
    try {
      testTransformer.jsonGenerator.flush();
      assertEquals(INITIAL_JSON + "{\"prop1\":\"propValue1\"}", response.getContentAsString());
    } catch (IOException e) {
      fail(e.getLocalizedMessage());
    }

    testTransformer.write((Object) result);
    testTransformer.assertMethodCallCounts(1, 2);

    try {
      testTransformer.jsonGenerator.flush();
      assertEquals(
          INITIAL_JSON + "{\"prop1\":\"propValue1\"},{\"prop2\":\"propValue2\"}",
          response.getContentAsString());
    } catch (IOException e) {
      fail(e.getLocalizedMessage());
    }

    testTransformer.end();
    testTransformer.assertMethodCallCounts(1, 2);

    try {
      assertEquals(
          INITIAL_JSON + "{\"prop1\":\"propValue1\"},{\"prop2\":\"propValue2\"}]}",
          response.getContentAsString());
    } catch (IOException e) {
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void getValueTest() {
    Map<String, Object> map = new HashMap<>();
    map.put("NotNull", "abc/");
    map.put("Null", null);
    assertEquals("abc/", testTransformer.getValue(map, "NotNull"));
    assertEquals("", testTransformer.getValue(map, "Null"));
    assertEquals("", testTransformer.getValue(map, "NoWay"));
  }
}
