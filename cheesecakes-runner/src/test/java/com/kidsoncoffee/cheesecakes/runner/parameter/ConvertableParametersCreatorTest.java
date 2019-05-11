package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.Scenario;
import com.kidsoncoffee.cheesecakes.runner.parameter.converter.ParameterConverterMethodsProvider;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import static com.kidsoncoffee.cheesecakes.ImmutableSchema.schema;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ConvertableParametersCreatorTest {
  @Test
  public void withoutSchemaParameters() {
    final Example.Builder example;
    final Method testMethod;
    final ConvertableParametersCreator creator;
    final Parameter.ConvertableParameter[] parameters;

    given:
    creator = new ConvertableParametersCreator();

    example =
        new Example.Builder(ConvertableParametersCreatorTest.class, "", Collections.emptyList());
    testMethod = retrieveMethod("methodReference");

    when:
    parameters = creator.create(testMethod, example);

    then:
    Assertions.assertThat(parameters).isEmpty();
  }

  @Test
  public void withSchemaParameters() {
    final Example.Builder example;
    final Method testMethod;
    final ConvertableParametersCreator creator;
    final Parameter.Schema firstParameterSchema, secondParameterSchema;
    final Parameter.ConvertableParameter[] parameters;
    final Object firstValue, secondValue;

    given:
    creator = new ConvertableParametersCreator();

    firstParameterSchema =
        schema()
            .name("firstParameter")
            .step(Scenario.StepType.REQUISITE)
            .overallOrder(0)
            .type(String.class)
            .build();
    secondParameterSchema =
        schema()
            .name("secondParameter")
            .step(Scenario.StepType.EXPECTATION)
            .overallOrder(1)
            .type(Integer.class)
            .build();

    firstValue = "A";
    secondValue = 1;

    example =
        new Example.Builder(
            ConvertableParametersCreatorTest.class,
            "",
            Arrays.asList(firstParameterSchema, secondParameterSchema));
    example.setValue(firstParameterSchema, firstValue);
    example.setValue(secondParameterSchema, secondValue);

    testMethod = retrieveMethod("methodReference");

    when:
    parameters = creator.create(testMethod, example);

    then:
    Assertions.assertThat(parameters[0].getSchema()).isEqualTo(firstParameterSchema);
    Assertions.assertThat(parameters[0].getMethod()).isEqualTo(testMethod);
    Assertions.assertThat(parameters[0].getValue()).isEqualTo(firstValue);

    Assertions.assertThat(parameters[1].getSchema()).isEqualTo(secondParameterSchema);
    Assertions.assertThat(parameters[1].getMethod()).isEqualTo(testMethod);
    Assertions.assertThat(parameters[1].getValue()).isEqualTo(secondValue.toString());
  }

  public void methodReference() {}

  /**
   * Returns a method with the given name from {@link ParameterConverterMethodsProvider}.
   *
   * @param methodName The name of the method to retrieve.
   * @return The method that matches the name.
   */
  private static Method retrieveMethod(final String methodName) {
    return Arrays.stream(ConvertableParametersCreatorTest.class.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "The setup for this test is incorrect. Method not found."));
  }
}
