package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.ImmutableSchema;
import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.Scenario;
import com.kidsoncoffee.cheesecakes.runner.parameter.converter.ParameterConverterMethodsProvider;
import com.kidsoncoffee.cheesecakes.runner.parameter.converter.ParameterConverterResolver;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.kidsoncoffee.cheesecakes.ImmutableConvertible.convertableParameter;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExampleParametersResolver}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleParametersResolverTest {

  /** The resolver of parameter converters. */
  private ParameterConverterResolver parameterConverterResolver;

  /** The unit under test. */
  private ExampleParametersResolver resolver;

  private ParameterConvertibleCreator parameterConvertibleCreator;

  /** Sets up the unit under test and its dependencies. */
  @Before
  public void setUp() {
    this.parameterConverterResolver = mock(ParameterConverterResolver.class);
    this.parameterConvertibleCreator = mock(ParameterConvertibleCreator.class);
    this.resolver =
        new ExampleParametersResolver(
            this.parameterConverterResolver, this.parameterConvertibleCreator);
  }

  /** Verify that mocks interactions are all accounted for. */
  @After
  public void verifyMockInteractions() {
    verifyNoMoreInteractions(this.parameterConverterResolver, this.parameterConvertibleCreator);
  }

  /** Checks that an empty optional is returned if no converters are found. */
  @Test
  public void noConvertersFound() {
    final Optional<Object[]> conversions;
    final Example.Builder example;
    final Method testMethod;

    given:
    // TODO fchovich feature class and scenario name are unnecessary here
    example = new Example.Builder(ExampleParametersResolverTest.class.getName(), "", Collections.emptyList());
    testMethod = retrieveMethod("withoutParameters");

    orchestrate:
    when(this.parameterConverterResolver.resolveConverters(testMethod, example))
        .thenReturn(Optional.empty());

    when:
    conversions = this.resolver.resolve(testMethod, example);

    then:
    Assertions.assertThat(conversions)
        .as("Should not be successful because the no converters were found.")
        .isNotPresent();

    verification:
    verify(this.parameterConverterResolver, times(1)).resolveConverters(testMethod, example);
  }

  /**
   * Checks that an empty optional is returned if no parameters are found in the scenario method.
   */
  @Test
  public void noParametersFound() {
    final Optional<Object[]> conversions;
    final Example.Builder example;
    final Method testMethod;

    given:
    // TODO fchovich feature class and scenario name are unnecessary here
    example = new Example.Builder(ExampleParametersResolverTest.class.getName(), "", Collections.emptyList());
    testMethod = retrieveMethod("withoutParameters");

    orchestrate:
    when(this.parameterConverterResolver.resolveConverters(testMethod, example))
        .thenReturn(
            Optional.of(
                new Parameter.Converter[] {
                  new ParameterConverterMethodsProvider.DummyConverter()
                }));
    when(this.parameterConvertibleCreator.create(testMethod, example))
        .thenReturn(new Parameter.Convertible[] {});

    when:
    conversions = this.resolver.resolve(testMethod, example);

    then:
    Assertions.assertThat(conversions)
        .as("Should not be successful because the no parameters were found.")
        .isNotPresent();

    verification:
    verify(this.parameterConverterResolver, times(1)).resolveConverters(testMethod, example);
    verify(this.parameterConvertibleCreator, times(1)).create(testMethod, example);
  }

  /** Checks that the resolver is able to resolve the parameter values for the example. */
  @Test
  public void resolveParameters() {
    final String firstParameterValue, secondParameterValue;
    final Parameter.Convertible firstParameter, secondParameter;
    final Parameter.Converter firstConverter;
    final Parameter.Converter secondConverter;
    final Optional<Object[]> conversions;
    final Example.Builder example;
    final Method testMethod;

    given:
    // TODO fchovich feature class and scenario name are unnecessary here
    example =
        new Example.Builder(
            ExampleParametersResolverTest.class.getName(),
            "",
            // TODO fchovich SIMPLIFY EXAMPLE BUILDER -> separate parts
            Arrays.asList(
                ImmutableSchema.schema()
                    .name("firstConverter")
                    .overallOrder(0)
                    .type(String.class)
                    .step(Scenario.StepType.REQUISITE)
                    .build(),
                ImmutableSchema.schema()
                    .name("secondConverter")
                    .overallOrder(1)
                    .type(String.class)
                    .step(Scenario.StepType.REQUISITE)
                    .build()));

    firstParameterValue = "A";
    secondParameterValue = "B";

    example.setValue("firstConverter", firstParameterValue);
    example.setValue("secondConverter", secondParameterValue);
    testMethod = retrieveMethod("withParameters");

    firstConverter = mock(Parameter.Converter.class, "firstConverter");
    secondConverter = mock(Parameter.Converter.class, "secondConverter");

    firstParameter =
        convertableParameter()
            .method(anyMethod())
            .schema(anySchema())
            .value(firstParameterValue)
            .build();
    secondParameter =
        convertableParameter()
            .method(anyMethod())
            .schema(anySchema())
            .value(secondParameterValue)
            .build();

    orchestrate:
    when(this.parameterConverterResolver.resolveConverters(testMethod, example))
        .thenReturn(
            Optional.of(
                new Parameter.Converter[] {
                  firstConverter, secondConverter,
                }));

    when(firstConverter.convert(firstParameter)).thenReturn("1");
    when(secondConverter.convert(secondParameter)).thenReturn("2");
    when(this.parameterConvertibleCreator.create(testMethod, example))
        .thenReturn(
            new Parameter.Convertible[] {
              convertableParameter()
                  .method(anyMethod())
                  .schema(anySchema())
                  .value(firstParameterValue)
                  .build(),
              convertableParameter()
                  .method(anyMethod())
                  .schema(anySchema())
                  .value(secondParameterValue)
                  .build(),
            });

    when:
    conversions = this.resolver.resolve(testMethod, example);

    then:
    Assertions.assertThat(conversions)
        .as("Both parameter values should have been converted.")
        .isPresent();

    Assertions.assertThat(conversions.get())
        .as("The converted parameter values matches the expected.")
        .containsExactly("1", "2");

    verification:
    verify(this.parameterConverterResolver, times(1)).resolveConverters(testMethod, example);
    verify(firstConverter, times(1)).convert(firstParameter);
    verify(secondConverter, times(1)).convert(secondParameter);
    verify(this.parameterConvertibleCreator, times(1)).create(testMethod, example);

    verifyNoMoreInteractions(firstConverter, secondConverter);
  }

  /** Method without parameters. */
  public void withoutParameters() {}

  /**
   * Method with parameters.
   *
   * @param first The first parameter.
   * @param second The second parameter.
   */
  public void withParameters(final String first, final String second) {}

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

  /**
   * Creates any method.
   *
   * @return Any method.
   * @deprecated Use a MOO/data generator pattern
   */
  @Deprecated
  private static Method anyMethod() {
    return retrieveMethod("withoutParameters");
  }

  /**
   * Creates any schema.
   *
   * @return Any schema.
   * @deprecated Use a MOO/data generator pattern
   */
  @Deprecated
  private static Parameter.Schema anySchema() {
    return ImmutableSchema.schema()
        .name("")
        .step(Scenario.StepType.REQUISITE)
        .type(String.class)
        .overallOrder(0)
        .build();
  }
}
