package examples;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.runner.Cheesecakes;
import examples.MyProgrammaticallyMultipleExamplesTest_ExampleBuilder.ConcatenatesSuccessfully;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.kidsoncoffee.cheesecakes.Example.multiple;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(Cheesecakes.class)
public class MyProgrammaticallyMultipleExamplesTest {

  private static Example.Multiple SUCCESS_EXAMPLES =
      multiple(
          ConcatenatesSuccessfully.given()
              .firstName("John")
              .lastName("Doe")
              .then()
              .completeName("John Doe")
              .getExample(),
          ConcatenatesSuccessfully.given()
              .firstName("Exene")
              .lastName("Cervenka")
              .then()
              .completeName("Exene Cervenka")
              .getExample());

  @Test
  public void concatenatesSuccessfully(
      @Parameter.Requisite final String firstName,
      @Parameter.Requisite final String lastName,
      @Parameter.Expectation final String completeName) {
    final String actualCompleteName;

    when:
    actualCompleteName = String.format("%s %s", firstName, lastName);

    then:
    assert actualCompleteName.equals(completeName);
  }
}
