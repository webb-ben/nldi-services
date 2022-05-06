package gov.usgs.owi.nldi;

import java.sql.Date;
import java.time.Instant;

import com.github.springtestdbunit.dataset.FlatXmlDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.springframework.core.io.Resource;

public class ColumnSensingFlatXMLDataSetLoader extends FlatXmlDataSetLoader {

	@Override
	protected IDataSet createDataSet(Resource resource) throws Exception {
		return createReplacementDataSet(super.createDataSet(resource));
	}

	private ReplacementDataSet createReplacementDataSet(IDataSet dataSet) {
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);

		replacementDataSet.addReplacementObject("[today]", Date.from(Instant.now()));
		replacementDataSet.addReplacementObject("[NULL]", null);

		return replacementDataSet;
	}

}
