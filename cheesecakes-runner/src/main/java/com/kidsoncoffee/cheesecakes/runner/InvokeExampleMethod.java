package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.parameter.ScenarioParametersConverter;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class InvokeExampleMethod extends Statement {
  private final FrameworkMethod testMethod;
  private final Object target;
  private final Example.Builder example;
  private final ScenarioParametersConverter scenarioParametersConverter;

  public InvokeExampleMethod(
      final FrameworkMethod testMethod,
      final Object target,
      final Example.Builder example,
      final ScenarioParametersConverter testBinder) {
    this.testMethod = testMethod;
    this.target = target;
    this.example = example;
    this.scenarioParametersConverter = testBinder;
  }

  @Override
  public void evaluate() throws Throwable {
    final Object[] parameters =
        this.scenarioParametersConverter.resolve(this.example, this.testMethod.getMethod());
    this.testMethod.invokeExplosively(this.target, parameters);
  }
}
