package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.runner.domain.ScenarioRunner;
import com.kidsoncoffee.cheesecakes.runner.example.DataTableExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.example.FieldExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.parameter.ScenarioParametersConverter;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class Cheesecakes extends Suite {

  private final ScenarioParametersConverter scenarioParametersConverter;

  private final ScenarioRunnerCreator scenarioRunnerCreator;

  private final List<Runner> runners;

  public Cheesecakes(final Class<?> klass) throws InitializationError {
    super(klass, Collections.emptyList());

    // TODO fchovich USE GUAVA
    this.scenarioParametersConverter = new ScenarioParametersConverter();
    this.scenarioRunnerCreator =
        new ScenarioRunnerCreator(
            Arrays.asList(new DataTableExamplesLoader(), new FieldExamplesLoader()),
            this.scenarioParametersConverter);

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
