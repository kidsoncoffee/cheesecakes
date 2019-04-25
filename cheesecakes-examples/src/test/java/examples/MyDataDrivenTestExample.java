package examples;

import com.kidsoncoffee.cheesecakes.runner.Cheesecakes;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(Cheesecakes.class)
public class MyDataDrivenTestExample {
  /**
   * Checks that the first and last name are concatenated correctly.
   *
   * <pre>
   * Examples:
   *
   * firstName | lastName || completeName
   * --------- | -------- || --------------
   * John      | Doe      || John Doe
   * Exene     | Cervenka || Exene Cervenka
   * </pre>
   */
  @Test
  public void concatenatesSuccessfully(
      final String firstName, final String lastName, final String completeName) {
    final String actualCompleteName;

    when:
    actualCompleteName = String.format("%s %s", firstName, lastName);

    then:
    assert actualCompleteName.equals(completeName);
  }
}
