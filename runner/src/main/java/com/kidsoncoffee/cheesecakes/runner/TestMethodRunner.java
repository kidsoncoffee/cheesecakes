package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.Specification;
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
public class TestMethodRunner extends BlockJUnit4ClassRunner {

  private final TestCase testCase;
  private final TestCaseParameterResolver testCaseParameterResolver;

  /**
   * Creates a BlockJUnit4ClassRunner to run {@code klass}
   *
   * @param klass
   * @param testCase
   * @param testCaseParameterResolver
   * @throws InitializationError if the testCase class is malformed.
   */
  public TestMethodRunner(
      final Class<?> klass,
      final TestCase testCase,
      final TestCaseParameterResolver testCaseParameterResolver)
      throws InitializationError {
    super(klass);
    this.testCase = testCase;
    this.testCaseParameterResolver = testCaseParameterResolver;
  }

  @Override
  protected String getName() {
    return this.testCase.getName();
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, Object test) {
    return new InvokeTestCase(
        method, test, this.testCase, this.testCaseParameterResolver);
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
    final Optional<String> binding = this.testCase.getBinding();

    return binding
        .map(s -> filterByBinding(tests, s))
        .orElseGet(() -> filterByType(tests, this.testCase.getSpecification().getClass()));
  }

  private List<FrameworkMethod> filterByBinding(List<FrameworkMethod> tests, String s) {
    return tests.stream()
        .filter(m -> m.getAnnotation(Parameters.ScenarioBinding.class).value().equals(s))
        .collect(Collectors.toList());
  }

  private List<FrameworkMethod> filterByType(
      List<FrameworkMethod> tests, Class<? extends Specification> bindingType) {
    return tests.stream()
        .filter(t -> t.getName().equalsIgnoreCase(bindingType.getSimpleName()))
        .collect(Collectors.toList());
  }
}
