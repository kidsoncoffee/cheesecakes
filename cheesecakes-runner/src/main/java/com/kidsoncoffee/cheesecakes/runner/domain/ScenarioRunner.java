package com.kidsoncoffee.cheesecakes.runner.domain;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

/**
 * Provides a {@link ParentRunner} for {@link ExampleRunner}. In this case representing a scenario.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ScenarioRunner extends ParentRunner<ExampleRunner> {

  /** The name of the scenario. */
  private final String name;

  /** The children runners. */
  private final List<ExampleRunner> runners;

  /**
   * Constructs a {@link ScenarioRunner}.
   *
   * @param testClass The test class of the scenario.
   * @param name The name of the scenario.
   * @param runners The example runners related to this scenario.
   * @throws InitializationError If any error occurs while initializing this runner.
   */
  public ScenarioRunner(
      final Class<?> testClass, final String name, final List<ExampleRunner> runners)
      throws InitializationError {
    super(testClass);
    this.name = name;
    this.runners = runners;
  }

  @Override
  protected String getName() {
    return this.name;
  }

  @Override
  public List<ExampleRunner> getChildren() {
    return this.runners;
  }

  @Override
  protected Description describeChild(final ExampleRunner child) {
    return Description.createTestDescription(
        this.getTestClass().getJavaClass().getName(), child.getName());
  }

  @Override
  protected void runChild(final ExampleRunner child, final RunNotifier notifier) {
    child.run(notifier);
  }
}
