package com.kidsoncoffee.paramtests;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ParameterizedTestMethodRunner extends BlockJUnit4ClassRunner {

  private final ParameterizedTest testParameters;

  /**
   * Creates a BlockJUnit4ClassRunner to run {@code klass}
   *
   * @param klass
   * @param testParameters
   * @throws InitializationError if the testParameters class is malformed.
   */
  public ParameterizedTestMethodRunner(Class<?> klass, ParameterizedTest testParameters)
      throws InitializationError {
    super(klass);
    this.testParameters = testParameters;
  }

  @Override
  protected void validateTestMethods(List<Throwable> errors) {
    // TODO fchovich WHAT SHOULD BE VALIDATED HERE
  }

  @Override
  protected String getName() {
    return this.testParameters.getName();
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, Object test) {
    return new InvokeParameterizedMethod(method, test, this.testParameters);
  }
}
