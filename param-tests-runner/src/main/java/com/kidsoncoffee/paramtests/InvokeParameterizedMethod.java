package com.kidsoncoffee.paramtests;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.function.Supplier;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class InvokeParameterizedMethod extends Statement {
  private final FrameworkMethod testMethod;
  private final Object target;
  private final ParameterizedTest testParameters;

  public InvokeParameterizedMethod(
      final FrameworkMethod testMethod, final Object target, ParameterizedTest testParameters) {
    this.testMethod = testMethod;
    this.target = target;
    this.testParameters = testParameters;
  }

  @Override
  public void evaluate() throws Throwable {
    final Object[] parameters =
        this.testParameters.getParameters().getInjectionables().stream()
            .map(Supplier::get)
            .toArray(Object[]::new);
    this.testMethod.invokeExplosively(this.target, parameters);
  }
}
