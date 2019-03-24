package com.kidsoncoffee.paramtests.runner;

import com.kidsoncoffee.paramtests.Scenario;
import com.kidsoncoffee.paramtests.annotations.Parameters;
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

  private final ParameterizedTestCase testParameters;

  /**
   * Creates a BlockJUnit4ClassRunner to run {@code klass}
   *
   * @param klass
   * @param testParameters
   * @throws InitializationError if the testParameters class is malformed.
   */
  public ParameterizedTestMethodRunner(
      final Class<?> klass, final ParameterizedTestCase testParameters) throws InitializationError {
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
    // SAME AS OVERRIDDEN
    validatePublicVoidNoArgMethods(After.class, false, errors);
    validatePublicVoidNoArgMethods(Before.class, false, errors);

    // TODO fchovich EXPAND VALIDATION
  }

  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    final List<FrameworkMethod> tests = super.computeTestMethods();
    final Optional<String> binding = this.testParameters.getBinding();

    return binding
        .map(s -> filterByBinding(tests, s))
        .orElseGet(() -> filterByType(tests, this.testParameters.getScenario().getClass()));
  }

  private List<FrameworkMethod> filterByBinding(List<FrameworkMethod> tests, String s) {
    return tests.stream()
        .filter(m -> m.getAnnotation(Parameters.ScenarioBinding.class).value().equals(s))
        .collect(Collectors.toList());
  }

  private List<FrameworkMethod> filterByType(
      List<FrameworkMethod> tests, Class<? extends Scenario> bindingType) {
    return tests.stream()
        .filter(t -> t.getName().equalsIgnoreCase(bindingType.getSimpleName()))
        .collect(Collectors.toList());
  }
}
