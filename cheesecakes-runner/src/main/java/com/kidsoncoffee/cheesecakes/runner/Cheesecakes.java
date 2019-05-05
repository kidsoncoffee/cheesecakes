package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.runner.domain.ScenarioRunner;
import com.kidsoncoffee.cheesecakes.runner.example.ClassExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.example.FieldExamplesLoader;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class Cheesecakes extends Suite {

  private final ScenarioRunnerCreator scenarioRunnerCreator;

  private final List<Runner> runners;

  public Cheesecakes(final Class<?> klass) throws InitializationError {
    super(klass, Collections.emptyList());

    // TODO fchovich USE GUAVA
    final ClassExamplesLoader classExamplesLoader = new ClassExamplesLoader();
    final FieldExamplesLoader fieldExamplesLoader = new FieldExamplesLoader();
    this.scenarioRunnerCreator =
        new ScenarioRunnerCreator(classExamplesLoader, fieldExamplesLoader);

    this.runners =
        createTestCaseRunners().stream().map(Runner.class::cast).collect(Collectors.toList());
  }

  private List<ScenarioRunner> createTestCaseRunners() throws InitializationError {
    return this.scenarioRunnerCreator.create(this.getTestClass());
  }

  @Override
  protected List<Runner> getChildren() {
    return this.runners;
  }
}
