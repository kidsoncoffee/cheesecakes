package com.kidsoncoffee.cheesecakes.runner;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class InvokeTestCase extends Statement {
  private final FrameworkMethod testMethod;
  private final Object target;
  private final TestCase testCase;
  private final TestCaseParameterResolver testBinder;

  public InvokeTestCase(
      final FrameworkMethod testMethod,
      final Object target,
      final TestCase testCase,
      final TestCaseParameterResolver testBinder) {
    this.testMethod = testMethod;
    this.target = target;
    this.testCase = testCase;
    this.testBinder = testBinder;
  }

  @Override
  public void evaluate() throws Throwable {
    final Object[] parameters = this.testBinder.resolve(this.testCase, this.testMethod.getMethod());
    this.testMethod.invokeExplosively(this.target, parameters);
  }
}
