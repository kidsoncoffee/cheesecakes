package examples;

import com.kidsoncoffee.paramtests.ScenarioBlock;
import com.kidsoncoffee.paramtests.annotations.Parameters;
import com.kidsoncoffee.paramtests.annotations.Parameters.Expectations;
import com.kidsoncoffee.paramtests.annotations.Parameters.Requisites;
import com.kidsoncoffee.paramtests.runner.ParameterizedTests;
import org.junit.Test;
import org.junit.runner.RunWith;

import static examples.SingleTestCaseExampleTestScenarios.SingleTestCase.given;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(ParameterizedTests.class)
public class SingleTestCaseExampleTest {

  // TODO fchovich create analysis tool for syntatic sugar
  @Parameters.Scenario("Full name test 1")
  private static final ScenarioBlock TEST_CASE =
      given().name("John").surname("Doe").then().expectedFullName("John Doe");

  @Parameters.Scenario("Full name test 2")
  private static final ScenarioBlock TEST_CASE_2 =
      given().name("Mary").surname("Doe").then().expectedFullName("Mary Doe");

  @Test
  public void singleTestCase(
      @Requisites final String name,
      @Requisites final String surname,
      @Expectations final String expectedFullName) {

    final String fullName;

    when:
    fullName = String.format("%s %s", name, surname);

    then:
    assert fullName.equals(expectedFullName);
  }
}
