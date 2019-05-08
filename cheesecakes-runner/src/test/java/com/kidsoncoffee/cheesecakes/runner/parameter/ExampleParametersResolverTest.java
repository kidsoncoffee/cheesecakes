package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.runner.parameter.converter.ParameterConverterMethodsProvider;
import org.assertj.core.api.Assertions;
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
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleParametersResolverTest {
  private ParameterConverterExtractor<Method> customConverterExtractor;
  private ParameterConverterExtractor<List<Parameter.Schema>> defaultConverterExtractor;
  private ExampleParametersResolver resolver;

  @Before
  public void setUp() {
    this.customConverterExtractor = Mockito.mock(ParameterConverterExtractor.class, "custom");
    this.defaultConverterExtractor = Mockito.mock(ParameterConverterExtractor.class, "default");

    this.resolver =
        new ExampleParametersResolver(
            this.customConverterExtractor, this.defaultConverterExtractor);
  }

  @Test
  public void convertersDifferentSize() {
    final Optional<Object[]> conversions;

    final Example.Builder example;
    final Method testMethod;
    final Optional<Parameter.Converter>[] defaultConverters;
    final Optional<Parameter.Converter>[] customConverters;

    given:
    //TODO fchovich feature class and scenario name are unnecessary here
    example = new Example.Builder(ExampleParametersResolverTest.class, "", Collections.emptyList());
    testMethod = retrieveMethod("differentSize");

    defaultConverters = new Optional[] {Optional.empty()};
    customConverters = new Optional[] {Optional.empty(), Optional.empty()};

    orchestrate:
    when(this.defaultConverterExtractor.extract(example.getSchema())).thenReturn(defaultConverters);
    when(this.customConverterExtractor.extract(testMethod)).thenReturn(customConverters);

    when:
    conversions = this.resolver.resolve(testMethod, example);

    then:
    Assertions.assertThat(conversions)
        .as(
            "Should not be successful because the default and custom converter does not match sizes.")
        .isNotPresent();

    verification:
    verify(this.defaultConverterExtractor, times(1)).extract(example.getSchema());
    verify(this.customConverterExtractor, times(1)).extract(testMethod);
    verifyNoMoreInteractions(this.customConverterExtractor, defaultConverterExtractor);
  }

  public void differentSize() {}

  /**
   * Returns a method with the given name from {@link ParameterConverterMethodsProvider}.
   *
   * @param methodName The name of the method to retrieve.
   * @return The method that matches the name.
   */
  private static Method retrieveMethod(final String methodName) {
    return Arrays.stream(ExampleParametersResolverTest.class.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "The setup for this test is incorrect. Method not found."));
  }
}
