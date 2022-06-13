package gov.usgs.owi.nldi.model;

import mil.nga.sf.geojson.*;

public class Feature {

  private Geometry geometry;
  private String type;
  private String source;
  private String sourceName;
  private String identifier;
  private String name;
  private String uri;
  private Integer comid;
  private String reachcode;
  private Double measure;
  private String mainstemUri;
  private String navigation;
  private String wellKnownText;

  public Feature(
      String type,
      String source,
      String sourceName,
      String identifier,
      String name,
      String uri,
      Integer comid,
      String reachcode,
      Double measure,
      String mainstemUri,
      Geometry geometry,
      String wellKnownText) {
    this.geometry = geometry;
    this.type = type;
    this.source = source;
    this.sourceName = sourceName;
    this.identifier = identifier;
    this.name = name;
    this.uri = uri;
    this.comid = comid;
    this.reachcode = reachcode;
    this.measure = measure;
    this.mainstemUri = mainstemUri == null ? "NA" : mainstemUri;
    this.wellKnownText = wellKnownText;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public Integer getComid() {
    return comid;
  }

  public void setComid(Integer comid) {
    this.comid = comid;
  }

  public String getReachcode() {
    return reachcode;
  }

  public void setReachcode(String reachcode) {
    this.reachcode = reachcode;
  }

  public Double getMeasure() {
    return measure;
  }

  public void setMeasure(Double measure) {
    this.measure = measure;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getSourceName() {
    return sourceName;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public String getNavigation() {
    return navigation;
  }

  public void setNavigation(String navigation) {
    this.navigation = navigation;
  }

  public String getMainstemUri() {
    return mainstemUri;
  }

  public void setMainstemUri(String mainstemUri) {
    this.mainstemUri = mainstemUri == null ? "NA" : mainstemUri;
  }

  public String getWellKnownText() {
    return wellKnownText;
  }

  public void setWellKnownText(String wellKnownText) {
    this.wellKnownText = wellKnownText;
  }
}
