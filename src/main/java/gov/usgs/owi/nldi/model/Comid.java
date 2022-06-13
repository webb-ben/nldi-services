package gov.usgs.owi.nldi.model;

import mil.nga.sf.geojson.Geometry;

public class Comid {

    private Geometry geometry;
    private static final String source = "comid";
    private static final String sourceName = "NHDPlus comid";
    private String identifier;
    private Integer comid;
    private String navigation;

    public Comid(String identifier, Integer comid, Geometry geometry) {
        this.geometry = geometry;
        this.identifier = identifier;
        this.comid = comid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getComid() {
        return comid;
    }

    public void setComid(Integer comid) {
        this.comid = comid;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getSource() {
        return source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getNavigation() {
        return navigation;
    }

    public void setNavigation(String navigation) {
        this.navigation = navigation;
    }
}
