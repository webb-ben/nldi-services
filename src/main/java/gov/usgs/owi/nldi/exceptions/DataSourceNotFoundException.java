package gov.usgs.owi.nldi.exceptions;

public class DataSourceNotFoundException extends RuntimeException {
    public DataSourceNotFoundException(String dataSource) {
        super(String.format("The data source \"%s\" does not exist.", dataSource));
    }
}
