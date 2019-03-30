package examples;

import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.Parameters.Expectations;
import com.kidsoncoffee.cheesecakes.Parameters.Requisites;
import com.kidsoncoffee.cheesecakes.runner.Cheesecakes;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(Cheesecakes.class)
// TODO fchovich CREATE RENAME PLUGIN (INVESTIGATE ANNOTATION PROCESSOR PERFORMANCE)
public class DataDrivenCommentTestCaseExampleTest {

  /**
   * WHERE:
   *
   * firstValue | secondValue | expectedSum
   * ---------- | ----------- | -----------
   * 1          | 2           | 3
   * 2          | 4           | 6
   */
  @Test
  @Parameters.DataDriven
  public void dataDrivenTestCase(
      // TODO fchovich VALIDATE FINALS
      @Requisites final int firstValue,
      @Requisites final int secondValue,
      @Expectations final int expectedSum) {
    final int sum;

    when:
    sum = firstValue + secondValue;

    then:
    assert sum == expectedSum;
  }
}
