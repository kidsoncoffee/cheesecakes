package com.kidsoncoffee.paramtests;

import org.junit.After;
import org.junit.Before;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
  public ParameterizedTestMethodRunner(final Class<?> klass, final ParameterizedTest testParameters)
      throws InitializationError {
    super(klass);
    this.testParameters = testParameters;
  }

  @Override
  protected String getName() {
    return this.testParameters.getName();
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, Object test) {
    return new InvokeParameterizedMethod(method, test, this.testParameters);
  }

  protected void validateInstanceMethods(List<Throwable> errors) {
    validatePublicVoidNoArgMethods(After.class, false, errors);
    validatePublicVoidNoArgMethods(Before.class, false, errors);

    // TODO fchovich EXPAND VALIDATION
  }

  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    final List<FrameworkMethod> tests = super.computeTestMethods();
    final Optional<String> binding = this.testParameters.getBinding();

    return binding
        .map(
            s ->
                tests.stream()
                    .filter(m -> m.getAnnotation(TestCaseBinding.class).value().equals(s))
                    .collect(Collectors.toList()))
        .orElse(tests);
  }
}
