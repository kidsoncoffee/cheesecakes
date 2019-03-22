package examples;

import com.kidsoncoffee.paramtests.ParameterizedTests;
import com.kidsoncoffee.paramtests.TestCaseBinding;
import com.kidsoncoffee.paramtests.TestCaseDefinition;
import com.kidsoncoffee.paramtests.TestCaseParametersBlock;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Expectations;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Requisites;
import examples.MultipleTestCaseExampleTestParameters.SubtractionTestCase;
import examples.MultipleTestCaseExampleTestParameters.SumTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(ParameterizedTests.class)
public class MultipleTestCaseExampleTest {

  // TODO fchovich CREATE CHECKSTYLE FOR TEST CASE DEFINITION (UNUSED CONSTANT)
  // TODO fchovich VALIDATE NO TEST CASE DEFINITIONS FOR RUNNER
  @TestCaseDefinition
  @TestCaseBinding("sum")
  private static final TestCaseParametersBlock SUM_A =
      SumTestCase.given().firstValue(1).secondValue(2).then().expectedSum(3);

  @TestCaseDefinition
  @TestCaseBinding("sum")
  private static final TestCaseParametersBlock SUM_B =
      SumTestCase.given().firstValue(99).secondValue(2).then().expectedSum(101);

  @TestCaseDefinition
  @TestCaseBinding("subtraction")
  private static final TestCaseParametersBlock SUBTRACTION =
      SubtractionTestCase.given().firstValue(3).secondValue(4).then().expectedSubtraction(-1);

  @Test
  @TestCaseBinding("sum")
  public void sumTestCase(
      @Requisites final int firstValue,
      @Requisites final int secondValue,
      @Expectations final int expectedSum) {

    final int sum;

    when:
    sum = firstValue + secondValue;

    then:
    assert sum == expectedSum;
  }

  @Test
  @TestCaseBinding("subtraction")
  // TODO fchovich SPECIFY TO USE A COMMON PARAMETER
  public void subtractionTestCase(
      @Requisites final int firstValue,
      @Requisites final int secondValue,
      @Expectations final int expectedSubtraction) {

    final int subtraction;

    when:
    subtraction = firstValue - secondValue;

    then:
    assert subtraction == expectedSubtraction;
  }
}
