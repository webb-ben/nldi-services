
package gov.usgs.owi.nldi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

	@Value("${nldi.displayProtocol}")
	private String displayProtocol;

	@Value("${nldi.displayHost}")
	private String displayHost;

	@Value("${nldi.displayPath}")
	private String displayPath;

	public String getDisplayProtocol() {
		return displayProtocol;
	}

	public String getDisplayHost() {
		return displayHost;
	}

	public String getDisplayPath() {
		return displayPath;
	}

	public String getRootUrl() {
		return getDisplayProtocol() + "://" + getDisplayHost() + getDisplayPath() + "/linked-data";
	}
}
