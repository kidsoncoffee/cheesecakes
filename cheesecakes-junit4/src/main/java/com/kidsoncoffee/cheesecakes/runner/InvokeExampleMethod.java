package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.parameter.ExampleParametersResolver;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.Optional;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class InvokeExampleMethod extends Statement {

  private final FrameworkMethod testMethod;
  private final Object target;
  private final Example.Builder example;

  /** The parameter converter. */
  private final ExampleParametersResolver parametersConverter;

  public InvokeExampleMethod(
      final ExampleParametersResolver parametersConverter,
      final FrameworkMethod testMethod,
      final Object target,
      final Example.Builder example) {
    this.parametersConverter = parametersConverter;
    this.testMethod = testMethod;
    this.target = target;
    this.example = example;
  }

  @Override
  public void evaluate() throws Throwable {
    final Optional<Object[]> parameters =
        this.parametersConverter.resolve(this.testMethod.getMethod(), this.example);

    if (parameters.isPresent()) {
      this.testMethod.invokeExplosively(this.target, parameters.get());
    } else {
      throw new CheesecakesException(
          String.format(
              "Unable to invoke test. Incorrect parameters for '%s' in '%s'.",
              this.testMethod.getName(), this.testMethod.getDeclaringClass()));
    }
  }
}
