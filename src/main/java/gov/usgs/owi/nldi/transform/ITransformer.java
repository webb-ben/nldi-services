package gov.usgs.owi.nldi.transform;

public interface ITransformer {

	void write(Object object);

	String encode(String value);
	
	void end();

}
