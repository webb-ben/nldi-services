package gov.usgs.owi.nldi.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSource {
	@JsonProperty("source")
	public String source;
	@JsonProperty("sourceName")
	public String sourceName;
	@JsonProperty("features")
	public String features;
}