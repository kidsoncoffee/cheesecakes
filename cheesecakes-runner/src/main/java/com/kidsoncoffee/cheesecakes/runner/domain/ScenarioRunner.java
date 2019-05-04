package com.kidsoncoffee.cheesecakes.runner.domain;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ScenarioRunner extends ParentRunner<ExampleRunner> {

  private final String name;

  private final List<ExampleRunner> runners;

  public ScenarioRunner(
      final Class<?> testClass, final String name, final List<ExampleRunner> runners)
      throws InitializationError {
    super(testClass);
    this.name = name;
    this.runners = runners;
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

  @Override
  protected Statement childrenInvoker(final RunNotifier notifier) {
    return super.childrenInvoker(notifier);
  }

  @Override
  protected String getName() {
    return this.name;
  }

  public List<ExampleRunner> getRunners() {
    return this.runners;
  }
}
