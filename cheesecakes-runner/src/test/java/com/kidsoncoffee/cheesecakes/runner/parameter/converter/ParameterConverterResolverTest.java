package com.kidsoncoffee.cheesecakes.runner.parameter.converter;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests of {@link ParameterConverterResolver}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ParameterConverterResolverTest {

  /** The custom converter extractor. */
  private ParameterConverterExtractor<Method> customConverterExtractor;

  /** The default converter extractor. */
  private ParameterConverterExtractor<List<Parameter.Schema>> defaultConverterExtractor;

  /** The unit under test. */
  private ParameterConverterResolver resolver;

  /** Sets up the unit under test and its dependencies. */
  @Before
  public void setUp() {
    this.customConverterExtractor = Mockito.mock(ParameterConverterExtractor.class, "custom");
    this.defaultConverterExtractor = Mockito.mock(ParameterConverterExtractor.class, "default");

    this.resolver =
        new ParameterConverterResolver(
            this.customConverterExtractor, this.defaultConverterExtractor);
  }

  /** Verify that mocks interactions are all accounted for. */
  @After
  public void verifyMockInteractions() {
    verifyNoMoreInteractions(this.customConverterExtractor, this.defaultConverterExtractor);
  }

  /**
   * Checks that the resolver returns an empty optional if the extractor does not return arrays of
   * the same size.
   */
  @Test
  public void convertersDifferentSize() {
    final Optional<Parameter.Converter[]> conversions;

    final Example.Builder example;
    final Method testMethod;
    final Optional<Parameter.Converter>[] defaultConverters;
    final Optional<Parameter.Converter>[] customConverters;

    given:
    // TODO fchovich feature class and scenario name are unnecessary here
    example =
        new Example.Builder(ParameterConverterResolverTest.class.getSimpleName(), "", Collections.emptyList());
    testMethod = retrieveMethod("noParameters");

    defaultConverters = new Optional[] {Optional.empty()};
    customConverters = new Optional[] {Optional.empty(), Optional.empty()};

    orchestrate:
    when(this.defaultConverterExtractor.extract(example.getSchema())).thenReturn(defaultConverters);
    when(this.customConverterExtractor.extract(testMethod)).thenReturn(customConverters);

    when:
    conversions = this.resolver.resolveConverters(testMethod, example);

    then:
    Assertions.assertThat(conversions)
        .as(
            "Should not be successful because the default and custom converter does not match sizes.")
        .isNotPresent();

    verification:
    verify(this.defaultConverterExtractor, times(1)).extract(example.getSchema());
    verify(this.customConverterExtractor, times(1)).extract(testMethod);
  }

  /** Checks that the resolver prioritizes the custom converter over the default converter. */
  @Test
  public void prioritizesCustomConverter() {
    final Optional<Parameter.Converter[]> converters;

    final Example.Builder example;
    final Method testMethod;
    final ParameterConverterMethodsProvider.DummyConverter customConverter;
    final Optional<Parameter.Converter>[] defaultConverters;
    final Optional<Parameter.Converter>[] customConverters;

    given:
    // TODO fchovich feature class and scenario name are unnecessary here
    example =
        new Example.Builder(ParameterConverterResolverTest.class.getSimpleName(), "", Collections.emptyList());
    testMethod = retrieveMethod("noParameters");

    customConverter = new ParameterConverterMethodsProvider.DummyConverter();

    defaultConverters = new Optional[] {Optional.empty()};
    customConverters = new Optional[] {Optional.of(customConverter)};

    orchestrate:
    when(this.defaultConverterExtractor.extract(example.getSchema())).thenReturn(defaultConverters);
    when(this.customConverterExtractor.extract(testMethod)).thenReturn(customConverters);

    when:
    converters = this.resolver.resolveConverters(testMethod, example);

    then:
    Assertions.assertThat(converters).as("Should return the custom converter.").isPresent();

    Assertions.assertThat(converters.get())
        .as("Should have only one resolved converter.")
        .hasSize(1)
        .as("The resolved converter should be the custom one.")
        .containsExactly(customConverter);

    verification:
    verify(this.defaultConverterExtractor, times(1)).extract(example.getSchema());
    verify(this.customConverterExtractor, times(1)).extract(testMethod);
  }

  /**
   * Checks that the resolver falls back to the default converter in the case of no custom
   * converters.
   */
  @Test
  public void fallbackToDefaultConverter() {
    final Optional<Parameter.Converter[]> converters;

    final Example.Builder example;
    final Method testMethod;
    final ParameterConverterMethodsProvider.DummyConverter defaultConverter;
    final Optional<Parameter.Converter>[] defaultConverters;
    final Optional<Parameter.Converter>[] customConverters;

    given:
    // TODO fchovich feature class and scenario name are unnecessary here
    example =
        new Example.Builder(ParameterConverterResolverTest.class.getSimpleName(), "", Collections.emptyList());
    testMethod = retrieveMethod("noParameters");

    defaultConverter = new ParameterConverterMethodsProvider.DummyConverter();

    defaultConverters = new Optional[] {Optional.of(defaultConverter)};
    customConverters = new Optional[] {Optional.empty()};

    orchestrate:
    when(this.defaultConverterExtractor.extract(example.getSchema())).thenReturn(defaultConverters);
    when(this.customConverterExtractor.extract(testMethod)).thenReturn(customConverters);

    when:
    converters = this.resolver.resolveConverters(testMethod, example);

    then:
    Assertions.assertThat(converters).as("Should return the custom converter.").isPresent();

    Assertions.assertThat(converters.get())
        .as("Should have only one resolved converter.")
        .hasSize(1)
        .as("The resolved converter should be the custom one.")
        .containsExactly(defaultConverter);

    verification:
    verify(this.defaultConverterExtractor, times(1)).extract(example.getSchema());
    verify(this.customConverterExtractor, times(1)).extract(testMethod);
  }

  /**
   * Checks that the resolver ignores parameter which could not match any custom or default
   * converter.
   */
  @Test
  public void ignoresParametersWithoutCustomNorDefaultConverters() {
    final Optional<Parameter.Converter[]> converters;

    final Example.Builder example;
    final Method testMethod;
    final ParameterConverterMethodsProvider.DummyConverter defaultConverter;
    final Optional<Parameter.Converter>[] defaultConverters;
    final Optional<Parameter.Converter>[] customConverters;

    given:
    // TODO fchovich feature class and scenario name are unnecessary here
    example =
        new Example.Builder(ParameterConverterResolverTest.class.getSimpleName(), "", Collections.emptyList());
    testMethod = retrieveMethod("noParameters");

    defaultConverters = new Optional[] {Optional.empty()};
    customConverters = new Optional[] {Optional.empty()};

    orchestrate:
    when(this.defaultConverterExtractor.extract(example.getSchema())).thenReturn(defaultConverters);
    when(this.customConverterExtractor.extract(testMethod)).thenReturn(customConverters);

    when:
    converters = this.resolver.resolveConverters(testMethod, example);

    then:
    Assertions.assertThat(converters).as("Should return the custom converter.").isPresent();

    Assertions.assertThat(converters.get()).as("Should no converters.").isEmpty();

    verification:
    verify(this.defaultConverterExtractor, times(1)).extract(example.getSchema());
    verify(this.customConverterExtractor, times(1)).extract(testMethod);
  }

  /** Method with no parameters. */
  public void noParameters() {}

  /**
   * Method with a couple parameters.
   *
   * @param first The first parameter.
   * @param second The second parameter.
   */
  public void coupleOfParameters(final String first, final String second) {}

  /**
   * Returns a method with the given name from {@link ParameterConverterMethodsProvider}.
   *
   * @param methodName The name of the method to retrieve.
   * @return The method that matches the name.
   */
  private static Method retrieveMethod(final String methodName) {
    return Arrays.stream(ParameterConverterResolverTest.class.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "The setup for this test is incorrect. Method not found."));
  }
}
