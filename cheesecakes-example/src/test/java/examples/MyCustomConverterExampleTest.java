package examples;

import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.Parameter.Conversion;
import com.kidsoncoffee.cheesecakes.Parameter.Expectation;
import com.kidsoncoffee.cheesecakes.Parameter.Requisite;
import com.kidsoncoffee.cheesecakes.runner.Cheesecakes;
import org.apache.commons.lang3.text.WordUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.function.Function;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@RunWith(Cheesecakes.class)
public class MyCustomConverterExampleTest {

  /**
   * Checks that the first and last name are concatenated correctly.
   *
   * <pre>
   * Examples:
   *
   * firstName | lastName | completeName
   * --------- | -------- | --------------
   * JOHN      | DOE      | John Doe
   * </pre>
   */
  @Test
  public void concatenatesSuccessfully(
      @Requisite @Conversion(PascalCaseConverter.class) final String firstName,
      @Requisite @Conversion(PascalCaseConverter.class) final String lastName,
      @Expectation final String completeName) {
    final String actualCompleteName;

    when:
    actualCompleteName = String.format("%s %s", firstName, lastName);

    then:
    assert actualCompleteName.equals(completeName);
  }

  private class PascalCaseConverter extends Parameter.Converter<String> {

    @Override
    public Class<String> getTargetType() {
      return String.class;
    }

    @Override
    public Function<Parameter.Convertible, String> getConverter() {
      return raw -> WordUtils.capitalize(raw.getValue().toLowerCase());
    }
  }
}
