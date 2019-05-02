package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.parameter.ScenarioParametersConverter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleRunner extends BlockJUnit4ClassRunner {

  private final String scenarioMethodName;
  private final Example.Builder example;
  private final ScenarioParametersConverter parametersResolver;

  public ExampleRunner(
      final Class featureClass,
      final String scenarioMethodName,
      final Example.Builder example,
      final ScenarioParametersConverter parametersResolver)
      throws InitializationError {
    super(featureClass);
    this.scenarioMethodName = scenarioMethodName;
    this.example = example;
    this.parametersResolver = parametersResolver;
  }

  @Override
  protected String getName() {
    return this.example.getSchema().stream()
        .map(schema -> format("%s=%s", schema.getName(), this.example.getValue(schema.getName())))
        .collect(Collectors.joining(", "));
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, Object test) {
    return new InvokeExample(method, test, this.example, this.parametersResolver);
  }

  protected void validateInstanceMethods(List<Throwable> errors) {
    // SAME AS OVERRIDDEN
    validatePublicVoidNoArgMethods(After.class, false, errors);
    validatePublicVoidNoArgMethods(Before.class, false, errors);

    // TODO fchovich EXPAND VALIDATION
  }

  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    return getTestClass().getAnnotatedMethods(Test.class).stream()
        .filter(t -> t.getName().equals(this.scenarioMethodName))
        .collect(Collectors.toList());
  }

  @Override
  protected String testName(FrameworkMethod method) {
    return this.getName();
  }

  @Override
  protected Description describeChild(FrameworkMethod method) {
    return Description.createTestDescription(
        this.getTestClass().getJavaClass().getName(), this.testName(method));
  }
}
