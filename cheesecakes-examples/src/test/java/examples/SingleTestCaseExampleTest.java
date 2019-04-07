package examples;

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
// TODO fchovich ALLOW PARAMETER NAME USE
public class SingleTestCaseExampleTest {

  // TODO fchovich create analysis tool for syntatic sugar
  /*@Parameters.Scenario("Full name test 1")
  private static final Spec TEST_CASE =
      given().name("John").surname("Doe").then().expectedFullName("John Doe");

  @Parameters.Scenario("Full name test 2")
  private static final Spec TEST_CASE_2 =
      given().name("Mary").surname("Doe").then().expectedFullName("Mary Doe");*/

  @Test
  public void singleTestCase(
      @Requisites("name") final String name,
      @Requisites("surname") final String surname,
      @Expectations("expectedFullName") final String expectedFullName) {

    final String fullName;

    when:
    fullName = String.format("%s %s", name, surname);

    then:
    assert fullName.equals(expectedFullName);
  }
}
