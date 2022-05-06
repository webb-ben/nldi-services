package gov.usgs.owi.nldi;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.dataset.ReplacementDataSetModifier;
import java.math.BigInteger;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.dbunit.dataset.ReplacementDataSet;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("it")
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  TransactionDbUnitTestExecutionListener.class
})
@DbUnitConfiguration(dataSetLoader = ColumnSensingFlatXMLDataSetLoader.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public abstract class BaseIT {

  private static final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  protected BigInteger id;

  protected class IdModifier extends ReplacementDataSetModifier {
    @Override
    protected void addReplacements(ReplacementDataSet dataSet) {
      dataSet.addReplacementSubstring("[id]", id.toString());
    }
  }

  protected class DateTimeModifier extends ReplacementDataSetModifier {
    @Override
    protected void addReplacements(ReplacementDataSet dataSet) {
      dataSet.addReplacementSubstring(
          "[dateTime]", LocalDateTime.now(Clock.systemUTC()).format(dateTimeFormatter));
    }
  }
}
