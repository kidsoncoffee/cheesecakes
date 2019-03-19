package examples;

import com.kidsoncoffee.paramtests.ParameterizedTests;
import com.kidsoncoffee.paramtests.TestCaseDefinition;
import com.kidsoncoffee.paramtests.TestCaseParametersBlock;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Expectations;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Requisites;
import org.junit.Test;
import org.junit.runner.RunWith;

import static examples.SingleTestCaseTestDefinitionExampleTestParameters.given;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(ParameterizedTests.class)
public class SingleTestCaseTestDefinitionExampleTest {

  // TODO fchovich create analysis tool for syntatic sugar
  @TestCaseDefinition
  private static final TestCaseParametersBlock TEST_CASE =
      given().name("John").surname("Doe").then().expectedFullName("John Doe");

  @TestCaseDefinition
  private static final TestCaseParametersBlock TEST_CASE_2 =
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
