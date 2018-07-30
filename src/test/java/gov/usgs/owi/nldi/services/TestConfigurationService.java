/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.owi.nldi.services;

/**
 *
 * @author mbucknel
 */
public class TestConfigurationService extends ConfigurationService {
	
	@Override
	public String getDisplayProtocol() {
		return "http";
	}
	
	@Override
	public String getDisplayHost() {
		return "owi-test.usgs.gov:8080";
	}
	
	@Override
	public String getDisplayPath() {
		return "/test-url";
	}
	
}
