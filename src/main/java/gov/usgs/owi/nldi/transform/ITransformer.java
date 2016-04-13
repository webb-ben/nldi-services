package gov.usgs.owi.nldi.transform;

public interface ITransformer {

	void write(Object object);

	void end();

}
