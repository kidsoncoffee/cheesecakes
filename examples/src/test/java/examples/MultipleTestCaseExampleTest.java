package examples;

import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.Parameters.Expectations;
import com.kidsoncoffee.cheesecakes.Parameters.Requisites;
import com.kidsoncoffee.cheesecakes.SpecificationBlock;
import com.kidsoncoffee.cheesecakes.frosting.Spec;
import com.kidsoncoffee.cheesecakes.runner.Cheesecakes;
import examples.MultipleTestCaseExampleTestParameters.SubtractionTestCase;
import examples.MultipleTestCaseExampleTestParameters.SumTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(Cheesecakes.class)
public class MultipleTestCaseExampleTest {

  // TODO fchovich CREATE CHECKSTYLE FOR TEST CASE DEFINITION (UNUSED CONSTANT)
  // TODO fchovich VALIDATE NO TEST CASE DEFINITIONS FOUND
  // TODO fchovich VALIDATE CROSS PACKAGE PARAMETER CLASS USAGE
  // TODO fchovich CREATE TESTS SORTER
  // TODO fchovich VALIDATE STATIC FINAL TEST CASES
  @Parameters.Scenario("Test Subtraction 1")
  private static final Spec SUM_A =
      SumTestCase.given().firstValue(1).secondValue(2).then().expectedSum(3);

  @Parameters.Scenario("Test Sum 1")
  private static final Spec SUM_B =
      SumTestCase.given().firstValue(99).secondValue(2).then().expectedSum(101);

  @Parameters.Scenario("Test Sum 2")
  private static final Spec SUBTRACTION =
      SubtractionTestCase.given().firstValue(3).secondValue(4).then().expectedSubtraction(-1);

  @Test
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
