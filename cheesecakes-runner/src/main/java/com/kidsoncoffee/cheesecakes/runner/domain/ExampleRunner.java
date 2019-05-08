package com.kidsoncoffee.cheesecakes.runner.domain;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.InvokeExampleMethod;
import com.kidsoncoffee.cheesecakes.runner.parameter.CustomConverterExtractor;
import com.kidsoncoffee.cheesecakes.runner.parameter.DefaultConverterExtractor;
import com.kidsoncoffee.cheesecakes.runner.parameter.DefaultParameterConverters;
import com.kidsoncoffee.cheesecakes.runner.parameter.ExampleParametersResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Provides a {@link BlockJUnit4ClassRunner}. In this case representing an example.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleRunner extends BlockJUnit4ClassRunner {

  /** The example. */
  private final Example.Builder example;

  /**
   * Constructs an example runner.
   *
   * @param featureClass The target feature class.
   * @param example The example.
   * @throws InitializationError If any error occurs while initializing the runner.
   */
  public ExampleRunner(final Class featureClass, final Example.Builder example)
      throws InitializationError {
    super(featureClass);
    this.example = example;
  }

  @Override
  protected String getName() {
    return this.example.getSchema().stream()
        .map(schema -> format("%s=%s", schema.getName(), this.example.getValue(schema.getName())))
        .collect(Collectors.joining(", "));
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, Object test) {
    final CustomConverterExtractor customConverterExtractor = new CustomConverterExtractor();
    final DefaultConverterExtractor defaultConverterExtractor =
        new DefaultConverterExtractor(
            Arrays.stream(DefaultParameterConverters.values())
                .map(DefaultParameterConverters::getConverter)
                .collect(Collectors.toList()));

    final ExampleParametersResolver parametersResolver =
        new ExampleParametersResolver(customConverterExtractor, defaultConverterExtractor);

    return new InvokeExampleMethod(parametersResolver, method, test, this.example);
  }

  protected void validateInstanceMethods(List<Throwable> errors) {
    // SAME AS OVERRIDDEN
    validatePublicVoidNoArgMethods(After.class, false, errors);
    validatePublicVoidNoArgMethods(Before.class, false, errors);

    // TODO fchovich EXPAND VALIDATION
  }

  /**
   * Returns the methods that run the tests. The method has to:
   *
   * <ul>
   *   <li>Be annotated with @{@link Test}
   *   <li>Match the name with the {@link Example.Builder#getScenarioMethodName()}
   * </ul>
   *
   * @return All methods matching the criteria above.
   */
  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    return getTestClass().getAnnotatedMethods(Test.class).stream()
        .filter(t -> t.getName().equals(this.example.getScenarioMethodName()))
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

  /**
   * Returns the example.
   *
   * @return The example.
   */
  public Example.Builder getExample() {
    return this.example;
  }
}
