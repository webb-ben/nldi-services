package gov.usgs.owi.nldi.transform;

public interface ITransformer extends AutoCloseable {

	void write(Object object);

	void end();

}
