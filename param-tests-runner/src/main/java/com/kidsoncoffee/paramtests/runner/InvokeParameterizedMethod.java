package com.kidsoncoffee.paramtests.runner;

import com.kidsoncoffee.paramtests.runner.ParameterizedTestCase;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class InvokeParameterizedMethod extends Statement {
  private final FrameworkMethod testMethod;
  private final Object target;
  private final ParameterizedTestCase testParameters;

  public InvokeParameterizedMethod(
      final FrameworkMethod testMethod, final Object target, ParameterizedTestCase testParameters) {
    this.testMethod = testMethod;
    this.target = target;
    this.testParameters = testParameters;
  }

  @Override
  public void evaluate() throws Throwable {
    /*final Object[] parameters =
        this.testParameters.getScenario().stream()
            .map(Supplier::get)
            .toArray(Object[]::new);
    this.testMethod.invokeExplosively(this.target, parameters);*/
  }
}
