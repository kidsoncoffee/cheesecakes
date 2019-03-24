package examples;

import com.kidsoncoffee.paramtests.annotations.Parameters;
import com.kidsoncoffee.paramtests.annotations.Parameters.Expectations;
import com.kidsoncoffee.paramtests.annotations.Parameters.Requisites;
import com.kidsoncoffee.paramtests.runner.ParameterizedTests;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(ParameterizedTests.class)
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
