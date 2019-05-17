package examples;

import com.kidsoncoffee.cheesecakes.Parameter.Expectation;
import com.kidsoncoffee.cheesecakes.Parameter.Requisite;
import com.kidsoncoffee.cheesecakes.runner.Cheesecakes;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(Cheesecakes.class)
public class MyDataDrivenExampleTest {

  /**
   * Checks that the first and last name are concatenated correctly.
   *
   * <pre>
   * Examples:
   *
   * firstName | lastName | completeName
   * --------- | -------- | --------------
   * John      | Doe      | John Doe
   * Exene     | Cervenka | Exene Cervenka
   * </pre>
   *
   * @param firstName The first name.
   * @param lastName The last name.
   * @param completeName The expected complete concatenated name.
   */
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
