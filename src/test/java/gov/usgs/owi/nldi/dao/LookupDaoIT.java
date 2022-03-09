package gov.usgs.owi.nldi.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import gov.usgs.owi.nldi.BaseIT;
import gov.usgs.owi.nldi.services.ConfigurationService;
import gov.usgs.owi.nldi.springinit.DbTestConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mil.nga.sf.geojson.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {DbTestConfig.class, LookupDao.class, ConfigurationService.class})
@DatabaseSetup("classpath:/testData/nldi_data/crawler_source.xml")
@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
public class LookupDaoIT extends BaseIT {

  @Autowired private LookupDao lookupDao;

  @Autowired private ConfigurationService configurationService;

  @Test
  public void getFeatureTest() {
    String featureSource = "wqp";
    String featureID = "USGS-05427880";
    Integer result = lookupDao.getFeatureComid(featureSource, featureID);
    assertEquals(13294132, result);
  }

  @Test
  public void getNullFeatureSourceTest() {
    String featureSource = null;
    String featureID = "USGS-05427880";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          lookupDao.getFeatureComid(featureSource, featureID);
        });
  }

  @Test
  public void getNullFeatureIDTest() {
    String featureSource = "wqp";
    String featureID = null;
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          lookupDao.getFeatureComid(featureSource, featureID);
        });
  }

  @Test
  public void getComidTest() {
    String featureSource = "comid";
    String featureID = "937090090";
    Integer result = lookupDao.getFeatureComid(featureSource, featureID);
    assertEquals(937090090, result);
  }

  @Test
  public void getComidLatLonTest() {
    Position position = new Position(-89.35, 43.0864);
    Integer results = lookupDao.getComidByLatitudeAndLongitude(position);
    assertEquals(13294318, results);
  }

  @Test
  public void getComidLatLonTestNotFound() {
    Position position = new Position(-89.4751, -89.4751);
    Integer results = lookupDao.getComidByLatitudeAndLongitude(position);
    assertNull(results);
  }

  @Test
  public void getDataSourcesTest() {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(LookupDao.ROOT_URL, configurationService.getLinkedDataUrl());
    List<Map<String, Object>> results = lookupDao.getList(BaseDao.DATA_SOURCES, parameterMap);
    assertFalse(results.isEmpty());
    assertEquals(5, results.size());
    assertEquals("huc12pp", results.get(0).get(LookupDao.SOURCE));
    assertEquals("huc12pp", results.get(0).get(LookupDao.SOURCE_NAME));
    assertEquals(
        String.join("/", configurationService.getLinkedDataUrl(), "huc12pp"),
        results.get(0).get(BaseDao.FEATURES));
    assertEquals("np21_nwis", results.get(1).get(LookupDao.SOURCE));
    assertEquals("HNDPlusV2_NWIS_Gages", results.get(1).get(LookupDao.SOURCE_NAME));
    assertEquals(
        String.join("/", configurationService.getLinkedDataUrl(), "np21_nwis"),
        results.get(1).get(BaseDao.FEATURES));
    assertEquals("nwissite", results.get(2).get(LookupDao.SOURCE));
    assertEquals("NWIS Surface Water Sites", results.get(2).get(LookupDao.SOURCE_NAME));
    assertEquals(
        String.join("/", configurationService.getLinkedDataUrl(), "nwissite"),
        results.get(2).get(BaseDao.FEATURES));
    assertEquals("TEST", results.get(3).get(LookupDao.SOURCE));
    assertEquals("TEST Source", results.get(3).get(LookupDao.SOURCE_NAME));
    assertEquals(
        String.join("/", configurationService.getLinkedDataUrl(), "test"),
        results.get(3).get(BaseDao.FEATURES));
    assertEquals("WQP", results.get(4).get(LookupDao.SOURCE));
    assertEquals("Water Quality Portal", results.get(4).get(LookupDao.SOURCE_NAME));
    assertEquals(
        String.join("/", configurationService.getLinkedDataUrl(), "wqp"),
        results.get(4).get(BaseDao.FEATURES));
  }
}
