package examples;

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
// TODO fchovich CREATE RENAME PLUGIN
public class DataDrivenBuilderTestCaseExampleTest {

  //@Parameters.Scenario("The sum of {firstValue} and {secondValue} should be {expectedSum}")
  // TODO fchovich CREATE SYNTATIC SUGAR (TEST CASE = TEST CASE PARAMETER BLOCK = MULTIPLE TEST CASE
  // PARAMETER)
  // TODO fchovich UNROLL TEST CASES NAMES OR KEEP IT UNDER
  /*private static final ScenarioBlock EXAMPLES =
  where(
      given().firstValue(1).secondValue(2).then().expectedSum(3),
      given().firstValue(2).secondValue(4).then().expectedSum(6));*/

  @Test
  public void dataDrivenTestCase(
      // TODO fchovich VALIDATE FINAL
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
