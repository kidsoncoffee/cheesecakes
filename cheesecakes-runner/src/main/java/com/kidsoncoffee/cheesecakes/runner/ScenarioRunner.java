package com.kidsoncoffee.cheesecakes.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;
import java.util.UUID;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ScenarioRunner extends ParentRunner<ExampleRunner> {

  private final String scenarioName;

  private final List<ExampleRunner> runners;

  public ScenarioRunner(Class<?> testClass, String scenarioName, List<ExampleRunner> runners)
      throws InitializationError {
    super(testClass);
    this.scenarioName = scenarioName;
    this.runners = runners;
  }

  @Override
  protected List<ExampleRunner> getChildren() {
    return this.runners;
  }

  @Override
  protected Description describeChild(ExampleRunner child) {
    return Description.createTestDescription(
        this.getTestClass().getJavaClass().getName(),
        child.getName());
  }

  @Override
  protected void runChild(ExampleRunner child, RunNotifier notifier) {
    // child.run(notifier);
    //    childrenInvoker()
    child.run(notifier);
  }

  @Override
  protected Statement childrenInvoker(RunNotifier notifier) {
    return super.childrenInvoker(notifier);
  }

  @Override
  protected String getName() {
    return this.scenarioName;
  }
}
