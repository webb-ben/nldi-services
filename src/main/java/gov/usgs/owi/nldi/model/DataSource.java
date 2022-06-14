package gov.usgs.owi.nldi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSource {
  @JsonProperty("source")
  private String source;

  @JsonProperty("sourceName")
  private String name;

  @JsonProperty("features")
  private String uri;

  public DataSource(String source, String name, String uri) {
    this.source = source;
    this.name = name;
    this.uri = uri;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
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
}
