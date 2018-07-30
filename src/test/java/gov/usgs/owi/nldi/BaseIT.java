package gov.usgs.owi.nldi;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.dbunit.dataset.ReplacementDataSet;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.util.FileCopyUtils;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.dataset.ReplacementDataSetModifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, 
	TransactionDbUnitTestExecutionListener.class
	})
@DbUnitConfiguration(dataSetLoader=ColumnSensingFlatXMLDataSetLoader.class)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional(propagation=Propagation.NOT_SUPPORTED)
public abstract class BaseIT {

	public static final String RESULT_FOLDER_WQP  = "feature/feature/wqp/";
	public static final String RESULT_FOLDER_HUC  = "feature/feature/huc12pp/";
	public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	protected BigInteger id;

	public String getCompareFile(String folder, String file) throws IOException {
		return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("testResult/" + folder + file).getInputStream()));
	}

	protected class IdModifier extends ReplacementDataSetModifier {
		@Override
		protected void addReplacements(ReplacementDataSet dataSet) {
			dataSet.addReplacementSubstring("[id]", id.toString());
		}
	}

	protected class DateTimeModifier extends ReplacementDataSetModifier {
		@Override
		protected void addReplacements(ReplacementDataSet dataSet) {
			dataSet.addReplacementSubstring("[dateTime]", LocalDateTime.now(Clock.systemUTC()).format(dtf));
		}
	}

}
