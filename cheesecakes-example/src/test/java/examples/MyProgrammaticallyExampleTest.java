package examples;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter.Expectation;
import com.kidsoncoffee.cheesecakes.Parameter.Requisite;
import com.kidsoncoffee.cheesecakes.runner.Cheesecakes;
import examples.MyProgrammaticallyExampleTest_ExampleBuilder.ConcatenatesSuccessfully;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(Cheesecakes.class)
public class MyProgrammaticallyExampleTest {

  // TODO fchovich ADD LOGGER WHEN EXAMPLES NOT FOUND FOR SCENARIO
  private static Example.Builder JOHN_DOE_EXAMPLE =
      ConcatenatesSuccessfully.given()
          .firstName("John")
          .lastName("Doe")
          .then()
          .completeName("John Doe")
          .getExample();

  private static Example.Builder EXENE_CERVENKA_EXAMPLE =
      ConcatenatesSuccessfully.given()
          .firstName("Exene")
          .lastName("Cervenka")
          .then()
          .completeName("Exene Cervenka")
          .getExample();

  @Test
  public void concatenatesSuccessfully(
      @Requisite final String firstName,
      @Requisite final String lastName,
      @Expectation final String completeName) {
    final String actualCompleteName;

    when:
    actualCompleteName = String.format("%s %s", firstName, lastName);

    then:
    assert actualCompleteName.equals(completeName);
  }
}
