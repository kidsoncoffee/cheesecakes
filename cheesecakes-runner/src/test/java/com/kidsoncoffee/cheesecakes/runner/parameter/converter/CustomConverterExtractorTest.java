package com.kidsoncoffee.cheesecakes.runner.parameter.converter;

import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.runner.parameter.converter.ParameterConverterMethodsProvider.DummyConverter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CustomConverterExtractor}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class CustomConverterExtractorTest {

  /** The unit under test. */
  private CustomConverterExtractor extractor;

  /** Sets up the unit under test. */
  @Before
  public void setUp() {
    this.extractor = new CustomConverterExtractor();
  }

  /** Checks that an empty list of converters is returned if the method has no parameters. */
  @Test
  public void methodWithoutParameters() {
    final Method method;
    final Optional<Parameter.Converter>[] converters;

    given:
    method = retrieveMethod("methodWithoutParameters");

    when:
    converters = this.extractor.extract(method);

    then:
    assertThat(converters)
        .as("A method without parameter should skip and return an empty array.")
        .isEmpty();
  }

  /**
   * Checks that a list of {@link Optional#empty()}s matching the number of parameters in the method
   * is returned when none of the parameters is annotated with {@link Parameter.Conversion}.
   */
  @Test
  public void methodWithoutConverters() {
    final Method method;
    final Optional<Parameter.Converter>[] converters;

    given:
    method = retrieveMethod("methodWithoutConverters");

    when:
    converters = this.extractor.extract(method);

    then:
    assertThat(converters)
        .as(
            "A method with parameters should return an array with the respective number of null items.")
        .hasSize(3)
        .containsExactly(Optional.empty(), Optional.empty(), Optional.empty());
  }

  /**
   * Checks that the extractor is able to assign custom converters to parameters annotated with
   * {@link Parameter.Conversion}.
   */
  @Test
  public void methodWithConverter() {
    final Method method;
    final Optional<Parameter.Converter>[] converters;

    given:
    method = retrieveMethod("methodWithConverter");

    when:
    converters = this.extractor.extract(method);

    then:
    assertThat(converters)
        .as("An array with the respective number of parameters should be returned.")
        .hasSize(2)
        .as("The parameters without converters should be empty.")
        .containsExactly(Optional.empty(), Optional.of(new DummyConverter()));
  }

  /** Checks that the extractor returns empty for parameter with invalid constructors. */
  @Test
  public void invalidConstructor() {
    final Method method;
    final Optional<Parameter.Converter>[] converters;

    given:
    method = retrieveMethod("invalidModifierConstructor");

    when:
    converters = this.extractor.extract(method);

    then:
    assertThat(converters)
        .as("An array with the respective number of empties should be returned.")
        .hasSize(2)
        .as("Converters with invalid constructors should return empty.")
        .containsExactly(Optional.empty(), Optional.empty());
  }

  @Test
  public void explodingConstructor() {
    final Method method;
    final Optional<Parameter.Converter>[] converters;

    given:
    method = retrieveMethod("explodingConstructor");

    when:
    converters = this.extractor.extract(method);

    then:
    assertThat(converters)
            .as("An array with the respective number of empties should be returned.")
            .hasSize(1)
            .as("Any exception during the instantiation of a converter should return empty.")
            .containsExactly(Optional.empty());
  }

  /**
   * Returns a method with the given name from {@link ParameterConverterMethodsProvider}.
   *
   * @param methodName The name of the method to retrieve.
   * @return The method that matches the name.
   */
  private static Method retrieveMethod(final String methodName) {
    return Arrays.stream(ParameterConverterMethodsProvider.class.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "The setup for this test is incorrect. Method not found."));
  }
}
