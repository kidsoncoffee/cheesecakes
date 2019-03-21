package examples;

import com.kidsoncoffee.paramtests.ParameterizedTests;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Expectations;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Requisites;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(ParameterizedTests.class)
public class MultipleTestCaseExampleTest {

  // TODO fchovich VALIDATE NO TEST CASE DEFINITIONS FOR RUNNER

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
